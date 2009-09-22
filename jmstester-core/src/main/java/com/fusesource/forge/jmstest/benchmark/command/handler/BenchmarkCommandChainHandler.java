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
package com.fusesource.forge.jmstest.benchmark.command.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;

public class BenchmarkCommandChainHandler implements BenchmarkCommandHandler {

  private Log log = null;
  private List<BenchmarkCommandHandler> handlerList = new ArrayList<BenchmarkCommandHandler>();

  public BenchmarkCommandChainHandler() {
    handlerList = new ArrayList<BenchmarkCommandHandler>();
  }

  public boolean handleCommand(BenchmarkCommand command) {

    log().debug("Handling Command: " + command);

    if (handlerList.size() == 0) {
      return false;
    }
    BenchmarkCommandHandler current = handlerList.get(0);

    boolean handled = false;
    while (current != null) {
      handled = current.handleCommand(command) || handled;
      current = current.next();
    }

    if (!handled) {
      log().debug("Not handled Command: " + command);
    }

    return handled;
  }

  public BenchmarkCommandHandler next() {
    return null;
  }

  public void setNext(BenchmarkCommandHandler next) {
    // do noting
  }

  public void addHandler(BenchmarkCommandHandler handler) {
    if (handlerList.size() > 0) {
      BenchmarkCommandHandler last = handlerList.get(handlerList.size() - 1);
      last.setNext(handler);
    }
    handlerList.add(handler);
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
