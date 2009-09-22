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
package com.fusesource.forge.jmstest.peristence;

import java.util.ArrayList;
import java.util.List;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.benchmark.command.ReportStatsCommand;
import com.fusesource.forge.jmstest.benchmark.command.transport.CommandTransport;
import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;

public class CommandSamplePersistenceAdapter extends
    CachingSamplePersistenceAdapter {

  private CommandTransport transport;
  private List<BenchmarkProbeValue> values;

  public CommandSamplePersistenceAdapter(ClientId clientId,
      CommandTransport transport) {
    super(clientId);
    this.transport = transport;
  }

  public CommandTransport getTransport() {
    return transport;
  }

  @Override
  protected void startFlush() {
    super.startFlush();
    values = new ArrayList<BenchmarkProbeValue>();
  }

  @Override
  protected void finishFlush() {
    super.finishFlush();
    BenchmarkCommand cmd = new ReportStatsCommand(getClientId(), values);
    getTransport().sendCommand(cmd);
  }

  @Override
  protected void flushValues(List<BenchmarkProbeValue> reportValues) {
    values.addAll(reportValues);
  }
}
