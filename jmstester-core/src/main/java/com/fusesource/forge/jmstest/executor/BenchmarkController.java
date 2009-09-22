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
package com.fusesource.forge.jmstest.executor;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkClientInfoCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCoordinator;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkGetClientInfo;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.handler.DefaultCommandHandler;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;

public class BenchmarkController extends AbstractBenchmarkExecutionContainer {

  private Log log = null;

  private Map<String, BenchmarkClientInfoCommand> clients = new TreeMap<String, BenchmarkClientInfoCommand>();
  private BenchmarkCoordinator coordinator = null;
  private BrokerService broker = null;

  @Override
  protected void createHandlerChain() {
    super.createHandlerChain();

    getConnector().addHandler(new DefaultCommandHandler() {
      public boolean handleCommand(BenchmarkCommand command) {
        if (command.getCommandType() == CommandTypes.CLIENT_INFO) {
          BenchmarkClientInfoCommand info = (BenchmarkClientInfoCommand) command;
          synchronized (clients) {
            clients.put(info.getClientName(), info);
          }
          return true;
        }
        return false;
      }
    });

    coordinator = new BenchmarkCoordinator();
    coordinator.setCommandTransport(getCmdTransport());
    coordinator.start();
    getConnector().addHandler(coordinator);
  }

  public void refreshClientInfos() {
    synchronized (clients) {
      clients.clear();
    }
    sendCommand(new BenchmarkGetClientInfo());
  }

  @Override
  synchronized public void start(String[] args) {
    log().info("Starting embedded broker for Benchmark framework: ");
    try {
      getBroker().start();
      broker.waitUntilStarted();
    } catch (Exception e) {
      log().error("Embedded broker could not be started.", e);
      stop();
    }
    super.start(args);
  }

  @Override
  synchronized public void stop() {

    coordinator.release();
    super.stop();

    log().info("BenchmarkController going down in 5 Seconds");

    final CountDownLatch brokerStopLatch = new CountDownLatch(1);
    final ScheduledThreadPoolExecutor waiter = new ScheduledThreadPoolExecutor(
        1);
    waiter.schedule(new Runnable() {
      public void run() {
        brokerStopLatch.countDown();
        waiter.shutdown();
      }
    }, 5, TimeUnit.SECONDS);

    try {
      brokerStopLatch.await();
    } catch (InterruptedException e1) {
    }

    if (broker != null) {
      log().info("Stopping embedded broker for Benchmark framework: ");
      try {
        broker.stop();
      } catch (Exception e) {
        // log().error("Embedded broker could not be stopped.", e);
      }
    }
  }

  private BrokerService getBroker() throws Exception {
    if (broker == null) {
      broker = new BrokerService();
      TransportConnector connector = broker.addConnector("tcp://0.0.0.0:"
          + getConnector().getPort());
      log().info("Clients can connect to " + connector.getUri().toString());
      setJmsPort(connector.getUri().getPort());
      broker.setPersistent(false);
      broker.setUseJmx(false);
      broker.start();
      broker.waitUntilStarted();
    }
    return broker;
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
