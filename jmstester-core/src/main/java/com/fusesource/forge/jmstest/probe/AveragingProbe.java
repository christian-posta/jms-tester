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

import java.util.concurrent.atomic.AtomicLong;

public class AveragingProbe extends AbstractProbe {

  private AtomicLong counter = new AtomicLong(0l);
  private double currentAverage = 0.0;

  public AveragingProbe() {
    super();
  }

  public AveragingProbe(String name) {
    super(name);
  }

  synchronized public void reset() {
    counter = new AtomicLong(0l);
    currentAverage = 0.0;
  }

  synchronized public void addValue(double value) {
    currentAverage = (currentAverage * counter.get() + value);
    counter.incrementAndGet();
    currentAverage /= counter.get();
  }

  @Override
  protected Number getValue() {
    return currentAverage;
  }
}
