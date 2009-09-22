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

import java.util.Random;

public class RandomProbe extends AbstractProbe {

  private double multiplier = 1000.0;
  private Random rnd = new Random();

  public RandomProbe() {
    super();
  }

  public RandomProbe(String name) {
    super(name);
  }

  public RandomProbe(double multiplier) {
    super();
    this.multiplier = multiplier;
  }

  public RandomProbe(String name, double multiplier) {
    super(name);
    this.multiplier = multiplier;
  }

  public Number getValue() {
    double result = rnd.nextDouble() * multiplier;
    return new Double(result);
  }
}
