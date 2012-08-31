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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.assertTrue;


public class ProbeRunnerTest implements Observer {

  private final static int RUN_COUNT = 10;

  public void update(Observable o, Object arg) {
    ((CountingProbe) ((Object) o)).increment();
  }

  @Test
  public void runProbes() {
    CountingProbe p = new CountingProbe("CountingProbe");
    p.addObserver(this);
    ProbeRunner pr = new ProbeRunner();
    pr.setDuration(RUN_COUNT);
    pr.setInterval(1);
    pr.setName("Test");
    List<Probe> probes = new ArrayList<Probe>();
    probes.add(p);
    pr.setProbes(probes);

    try {
      pr.start();
      pr.waitUntilFinished();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    assertTrue((RUN_COUNT - 1 <= p.getValue().intValue()) && (p.getValue().intValue() <= RUN_COUNT + 1));
  }
}
