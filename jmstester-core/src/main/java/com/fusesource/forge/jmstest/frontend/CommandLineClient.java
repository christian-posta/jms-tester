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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.ShutdownCommand;
import com.fusesource.forge.jmstest.benchmark.command.SubmitBenchmarkCommand;
import com.fusesource.forge.jmstest.executor.AbstractBenchmarkExecutor;

public class CommandLineClient extends AbstractBenchmarkExecutor {

  private Log log = null;
  private List<BenchmarkCommand> commands = null;

  public void setCommand(String command) {
    if (command.equals("shutdown")) {
      commands.add(new ShutdownCommand());
    }

    if (command.startsWith("submit:")) {
      String configDirs = command.substring("submit:".length());
      ApplicationContext appContext = getApplicationContext(configDirs);
      String[] beanNames = appContext
          .getBeanNamesForType(BenchmarkConfig.class);
      for (String name : beanNames) {
        BenchmarkConfig cfg = (BenchmarkConfig) appContext.getBean(name);
        cfg.getSpringConfigurations();
        log().info("Submitting benchmark: " + cfg.getBenchmarkId());
        commands.add(new SubmitBenchmarkCommand(cfg));
      }
    }
  }

  void run(String[] args) {
    commands = new ArrayList<BenchmarkCommand>();
    handleArguments(args);
    for (BenchmarkCommand cmd : commands) {
      getCmdTransport().sendCommand(cmd);
    }
    getCmdTransport().stop();
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }

  public static void main(String[] args) {
    new CommandLineClient().run(args);
  }
}
