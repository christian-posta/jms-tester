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

import org.testng.Assert;
import org.testng.annotations.Test;

public class ProbesTest {

  @Test
  public void testCountingProbe() {
    CountingProbe probe = new CountingProbe();
    Assert.assertEquals(probe.getValue(), new Long(0L));

    probe.increment();
    Assert.assertEquals(probe.getValue(), new Long(1L));

    probe.increment(10L);
    Assert.assertEquals(probe.getValue(), new Long(11L));
  }

  @Test
  public void testAveragingProbe() {

    int count = 100;

    AveragingProbe probe = new AveragingProbe();
    Assert.assertEquals(probe.getValue(), 0.0);

    for (int i = 0; i < count; i++) {
      probe.addValue(1);
    }
    Assert.assertEquals(probe.getValue(), 1.0);

    probe.addValue(count);
    Assert.assertEquals(probe.getValue(), 200.0 / (count + 1));

    probe.reset();
    Assert.assertEquals(probe.getValue(), 0.0);
  }
}
