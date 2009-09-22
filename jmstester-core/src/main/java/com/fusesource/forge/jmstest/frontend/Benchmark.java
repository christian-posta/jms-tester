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
package com.fusesource.forge.jmstest.frontend;

import java.util.StringTokenizer;

import com.fusesource.forge.jmstest.executor.AbstractBenchmarkExecutor;
import com.fusesource.forge.jmstest.executor.BenchmarkClient;
import com.fusesource.forge.jmstest.executor.BenchmarkController;
import com.fusesource.forge.jmstest.executor.BenchmarkValueRecorder;

public class Benchmark extends AbstractBenchmarkExecutor {

  private boolean controller = false;
  private String clientNames = null;
  private boolean recorder = false;

  public boolean isClient() {
    return (getClientNames() != null);
  }

  public String getClientNames() {
    return clientNames;
  }

  public void setClientNames(String clientNames) {
    this.clientNames = clientNames;
  }

  public boolean isRecorder() {
    return recorder;
  }

  public void setRecorder(String recorder) {
    try {
      this.recorder = new Boolean(recorder).booleanValue();
    } catch (Exception e) {
    }
  }

  public boolean isController() {
    return controller;
  }

  public void setController(String controller) {
    try {
      this.controller = new Boolean(controller).booleanValue();
    } catch (Exception e) {
    }
  }

  public void start(String[] args) {
    handleArguments(args);

    if (isController()) {
      BenchmarkController controller = new BenchmarkController();
      controller.setAutoTerminate(false);
      controller.start(args);
    }
    if (isRecorder()) {
      BenchmarkValueRecorder recorder = new BenchmarkValueRecorder();
      recorder.setAutoTerminate(false);
      recorder.start(args);
    }
    if (isClient()) {
      StringTokenizer sTok = new StringTokenizer(getClientNames(), ",");
      while (sTok.hasMoreTokens()) {
        String clientName = sTok.nextToken();
        BenchmarkClient client = new BenchmarkClient();
        client.setName(clientName);
        client.setAutoTerminate(false);
        client.start(args);
      }
    }
  }

  public static void main(String[] args) {
    final Benchmark bm = new Benchmark();
    bm.start(args);
  }
}
