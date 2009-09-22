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

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue.ValueType;

public class CountingProbe extends AbstractProbe {

  private AtomicLong counter = new AtomicLong(0l);

  public CountingProbe() {
    super();
  }

  public CountingProbe(String name) {
    super(name);
  }

  public long increment() {
    return counter.incrementAndGet();
  }

  public long increment(long delta) {
    counter.getAndAdd(delta);
    return counter.get();
  }

  @Override
  public ValueType getValueType() {
    return ValueType.COUNTER;
  }

  @Override
  public Number getValue() {
    return counter.longValue();
  }

  @Override
  public void reset() {
    counter.set(0l);
  }
}
