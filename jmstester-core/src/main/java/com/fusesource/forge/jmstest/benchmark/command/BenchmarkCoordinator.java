/*
 * Copyright (C) 2009, Progress Software Corporation and/or its
 * subsidiaries or affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fusesource.forge.jmstest.benchmark.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.handler.DefaultCommandHandler;
import com.fusesource.forge.jmstest.benchmark.command.transport.CommandTransport;
import com.fusesource.forge.jmstest.executor.BenchmarkRunStatus;
import com.fusesource.forge.jmstest.executor.ReleaseManager;
import com.fusesource.forge.jmstest.executor.Releaseable;

public class BenchmarkCoordinator extends DefaultCommandHandler 
  implements Releaseable {

  private Map<String, BenchmarkRunner> benchmarks;
  private ExecutorService executor = null;
  private Boolean started = false;
  private Log log = null;
  private CommandTransport cmdTransport = null;

  @Override
  public boolean handleCommand(BenchmarkCommand command) {
    switch (command.getCommandType()) {
    case CommandTypes.SUBMIT_BENCHMARK:
      synchronized (benchmarks) {
        SubmitBenchmarkCommand sbc = (SubmitBenchmarkCommand) command;
        BenchmarkRunner runner = new BenchmarkRunner(
          getCommandTransport(), sbc.getBenchmarkConfig()
        );
        benchmarks.put(sbc.getBenchmarkConfig().getBenchmarkId(), runner);
        executor.submit(runner);
      }
      return true;
    case CommandTypes.PREPARE_RESPONSE:
      synchronized (benchmarks) {
        PrepareBenchmarkResponse response = (PrepareBenchmarkResponse) command;
        BenchmarkRunner runner = benchmarks.get(response.getBenchmarkId());
        if (runner != null) {
          runner.registerClient(response);
          return true;
        } else {
          return false;
        }
      }
    case CommandTypes.PRODUCER_FINISHED:
      synchronized (benchmarks) {
        ProducerFinishedCommand response = (ProducerFinishedCommand) command;
        BenchmarkRunner runner = benchmarks.get(response.getBenchmarkId());
        if (runner != null) {
          runner.finishProducer(response);
          return true;
        } else {
          return false;
        }
      }
    default:
      return false;
    }
  }

  public CommandTransport getCommandTransport() {
    return cmdTransport;
  }

  public void setCommandTransport(CommandTransport cmdTransport) {
    this.cmdTransport = cmdTransport;
  }

  public void start() {
    synchronized (started) {
      if (!started) {
        log().debug("Starting Benchmark Coordinator.");
        ReleaseManager.getInstance().register(this);
        benchmarks = new HashMap<String, BenchmarkRunner>();
        executor = new ThreadPoolExecutor(
          1, 1, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()
        );
        started = true;
      }
    }
  }

  public void waitUntilFinished() {
    if (executor != null) {
      while (!executor.isShutdown()) {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException ie) {
        }
      }
      try {
        executor.awaitTermination(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
      }
    }
  }

  public void release() {
    synchronized (started) {
      if (executor != null) {
        executor.shutdown();
      }
    }
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }

  private class BenchmarkRunner implements Runnable {

    private BenchmarkConfig config;
    private CommandTransport cmdTransport;

    private int producerClients = 0;
    private int consumerClients = 0;

    private CountDownLatch latch;

    private Log log = null;

    private Map<String, BenchmarkRunStatus> clientStates;

    public BenchmarkRunner(CommandTransport cmdTransport, BenchmarkConfig config) {
      this.cmdTransport = cmdTransport;
      this.config = config;
    }

    public void registerClient(PrepareBenchmarkResponse response) {

      ClientId clientId = response.getClientId();
      log().debug("Prepare Response received: " + clientId);

      synchronized (clientStates) {
        if (clientStates.containsKey(clientId)) {
          BenchmarkRunStatus state = clientStates.get(clientId);
          if (BenchmarkRunStatus.State.PREPARED == state.getCurrentState()) {
            log()
                .warn(
                    "Benchmark already knew this client. Are you using unique client id's for the agents?");
          }
        } else {
          BenchmarkRunStatus state = new BenchmarkRunStatus();
          state.setState(BenchmarkRunStatus.State.PREPARED);
          clientStates.put(clientId.toString(), state);
        }
      }
    }

    public void finishProducer(ProducerFinishedCommand finished) {
      ClientId clientId = finished.getClientId();
      log().debug("Producer finished: " + clientId);

      synchronized (clientStates) {
        if (clientStates.containsKey(clientId.toString())) {
          BenchmarkRunStatus state = clientStates.get(clientId.toString());
          if (state.getCurrentState() != BenchmarkRunStatus.State.FINISHED) {
            state.setState(BenchmarkRunStatus.State.FINISHED);
            latch.countDown();
          }
        }
      }
    }

    private boolean waitForClients(long timeOut) {

      boolean result = true;

      try {
        Thread.sleep(timeOut);
      } catch (InterruptedException ie) {
      }

      // lets have a look at the clients ...

      int prepared = 0;

      synchronized (clientStates) {
        log()
            .debug(
                "Examining " + clientStates.size()
                    + " client pepare responses ...");
        for (String clientId : clientStates.keySet()) {
          BenchmarkRunStatus state = clientStates.get(clientId);
          if (BenchmarkRunStatus.State.PREPARED == state.getCurrentState()) {
            prepared++;
          } else {
            log().error(
                "The pre registered client (" + clientId
                    + ") was not prepared. The benchmark won't run.");
            result = false;
          }
          if (clientId.startsWith("" + ClientType.CONSUMER)) {
            consumerClients++;
          }
          if (clientId.startsWith("" + ClientType.PRODUCER)) {
            producerClients++;
          }
        }
      }

      if (producerClients == 0) {
        log().error(
            "No producers are registered for benchmark: "
                + config.getBenchmarkId() + ". The benchmark won't run.");
        result = false;
      }
      if (consumerClients == 0) {
        log().error(
            "No consumers are registered for benchmark: "
                + config.getBenchmarkId() + ". The benchmark won't run.");
        result = false;
      }
      return result;
    }

    public void run() {
      log().info("Executing Benchmark: " + config.getBenchmarkId());

      clientStates = new HashMap<String, BenchmarkRunStatus>();

      synchronized (clientStates) {
        for (BenchmarkPartConfig partCfg : config.getBenchmarkParts()) {
          partCfg.getPartID();
        }
      }

      cmdTransport.sendCommand(new PrepareBenchmarkCommand(config));

      // TODO: make this interrupable and stuff

      if (waitForClients(5000)) {
        log().debug("Executing benchmark ...");
        cmdTransport.sendCommand(new StartBenchmarkCommand(config
            .getBenchmarkId()));
        latch = new CountDownLatch(producerClients);
        try {
          latch.await();
        } catch (InterruptedException ie) {
        }
        log().debug("All producers have finished ... ");
        cmdTransport.sendCommand(new EndBenchmarkCommand(config
            .getBenchmarkId()));
      } else {
        log().error(
            "Invalid client configuration for benchmark ("
                + config.getBenchmarkId() + "). Execution skipped.");
      }
      log().info("Finished Benchmark: " + config.getBenchmarkId());
    }

    private Log log() {
      if (log == null) {
        log = LogFactory.getLog(this.getClass());
      }
      return log;
    }
  }
}
