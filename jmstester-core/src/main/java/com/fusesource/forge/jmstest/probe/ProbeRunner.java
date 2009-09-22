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
package com.fusesource.forge.jmstest.probe;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.executor.LimitedTimeScheduledExecutor;

public class ProbeRunner extends LimitedTimeScheduledExecutor {

  private List<Probe> probes;

  public ProbeRunner() {
    probes = new ArrayList<Probe>();
  }

  synchronized public void setProbes(List<Probe> probes) {
    if (probes != null) {
      this.probes = probes;
    }
  }

  public List<Probe> getProbes() {
    return probes;
  }

  public void addProbe(Probe probe) {
    synchronized (probes) {
      getProbes().add(probe);
    }
  }

  public void run() {
    setTask(new Runnable() {
      public void run() {
        log().debug("Gathering probes ...");
        synchronized (probes) {
          for (Probe probe : getProbes()) {
            probe.probe();
          }
        }
        log().debug("Gathering complete ...");
      }
    });
    super.run();
  }

  private Log log() {
    return LogFactory.getLog(this.getClass());
  }
}
