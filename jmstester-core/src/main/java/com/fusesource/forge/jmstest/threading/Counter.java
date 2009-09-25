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
package com.fusesource.forge.jmstest.threading;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Counter extends AbstractSteppable {
  
  final static private int MAX_COUNTER = 100;
  private int count = 0;
  
  private Log log = null;
  
  public Counter(String name) {
    super(name);
  }

  protected void doStep() {
    count++;
    log().info("Counter(" + getName() + ") :" + count);
  }

  @Override
  protected boolean needsMoreSteps() {
    return count < MAX_COUNTER;
  }
  
  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
