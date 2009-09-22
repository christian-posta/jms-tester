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
package com.fusesource.forge.jmstest.benchmark.command;

import java.util.List;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;

public class ReportStatsCommand extends BaseBenchmarkCommand {

  private static final long serialVersionUID = -5141521547392349990L;

  private ClientId clientId = null;
  private List<BenchmarkProbeValue> values;

  public ReportStatsCommand(ClientId clientId, List<BenchmarkProbeValue> values) {
    this.clientId = clientId;
    this.values = values;
  }

  public ClientId getClientId() {
    return clientId;
  }

  public List<BenchmarkProbeValue> getValues() {
    return values;
  }

  public byte getCommandType() {
    return CommandTypes.REPORT_STATS;
  }

  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer("ReportStatsCommand[");
    buf.append(getClientId().toString());
    buf.append(",");
    buf.append(values.size());
    buf.append("]");
    return buf.toString();
  }
}
