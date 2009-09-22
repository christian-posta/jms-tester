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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.peristence.BenchmarkSamplePersistenceAdapter;
import com.fusesource.forge.jmstest.peristence.CommandSamplePersistenceAdapter;
import com.fusesource.forge.jmstest.probe.Probe;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;
import com.fusesource.forge.jmstest.probe.ProbeRunner;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public abstract class AbstractBenchmarkClient implements Releaseable {

  private BenchmarkConfig config = null;
  private BenchmarkClient container;

  private ProbeRunner probeRunner = null;
  private BenchmarkSamplePersistenceAdapter adapter = null;

  private Log log = null;

  public AbstractBenchmarkClient(
    BenchmarkClient container,
    BenchmarkConfig config
  ) {
    this.config = config;
    this.container = container;
  }

  public BenchmarkClient getContainer() {
    return container;
  }

  public BenchmarkConfig getConfig() {
    return config;
  }

  public ApplicationContext getApplicationContext() {
    return getConfig().getApplicationContext();
  }

  public BenchmarkSamplePersistenceAdapter getSamplePersistenceAdapter() {
    if (adapter == null) {
      adapter = new CommandSamplePersistenceAdapter(getClientId(),
          getContainer().getCmdTransport());
    }
    return adapter;
  }

  protected ProbeRunner getProbeRunner() {
    long duration = Long.MIN_VALUE;
    for (BenchmarkPartConfig partConfig : getConfig().getBenchmarkParts()) {
      try {
        BenchmarkIteration iteration = getIteration(partConfig.getProfileName());
        long partDuration = iteration.getTotalDuration();
        if (partDuration > duration) {
          duration = partDuration;
        }
      } catch (Exception e) {
        // ignore
      }
    }
    if (probeRunner == null) {
      probeRunner = new ProbeRunner();
      probeRunner.setName(getClientId().toString());
      probeRunner.setInterval(1);
      probeRunner.setDuration(duration);
    }
    return probeRunner;
  }

  public abstract ClientType getClientType();

  public abstract ClientId getClientId();

  public boolean prepare() {
    ReleaseManager.getInstance().register(this);
    return true;
  }

  public void start() {
    if (getSamplePersistenceAdapter() != null) {
      getSamplePersistenceAdapter().start();
    }
    if (getProbeRunner() != null) {
      getProbeRunner().start();
    }
  }

  public void release() {
    if (adapter != null) {
      adapter.stop();
    }
    if (probeRunner != null) {
      probeRunner.release();
    }
    ReleaseManager.getInstance().deregister(this);
  }

  @SuppressWarnings("unchecked")
  public Object getBean(String[] names, Class type) {
    Object result = null;

    for (String name : names) {
      try {
        log().debug(
            "Retrieving bean from Benchmark Application Context: " + name);
        result = getApplicationContext().getBean(name);
        return result;
      } catch (Exception e) {
        // ignore
      }
    }

    if (type != null) {
      log().debug(
          "Bean not found, trying to resolve by type: " + type.getName());
      String[] beanNames = getApplicationContext().getBeanNamesForType(type);
      if (beanNames.length == 0) {
        log().warn("Could not resolve bean by type: " + type.getName());
      } else {
        if (beanNames.length > 1) {
          log().warn("Multiple beans found for type: " + type.getName());
        }
        result = getApplicationContext().getBean(beanNames[0]);
      }
    }

    return result;
  }

  public List<ProbeDescriptor> getProbeDescriptors() {
    List<ProbeDescriptor> result = new ArrayList<ProbeDescriptor>();
    ProbeRunner pr = getProbeRunner();
    if (pr != null) {
      List<Probe> probes = pr.getProbes();
      if (probes != null) {
        for (Probe p : probes) {
          result.add(p.getDescriptor());
        }
      }
    }
    return result;
  }

  synchronized public BenchmarkIteration getIteration(String profileName) {

    BenchmarkIteration iteration = null;

    try {
      iteration = (BenchmarkIteration) getApplicationContext().getBean(
          profileName);
    } catch (Exception e) {
      log().error("Error creating Iteration.", e);
    }
    return iteration;
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
