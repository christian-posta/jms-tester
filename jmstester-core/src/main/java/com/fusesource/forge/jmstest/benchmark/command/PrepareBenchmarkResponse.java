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

import com.fusesource.forge.jmstest.executor.AbstractBenchmarkClient;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public class PrepareBenchmarkResponse extends ProducerFinishedCommand {

  private static final long serialVersionUID = -5586700527088404915L;
  private List<ProbeDescriptor> clientProbes;

  public PrepareBenchmarkResponse(AbstractBenchmarkClient client) {
    super(client);
    this.clientProbes = client.getProbeDescriptors();
  }

  public byte getCommandType() {
    return CommandTypes.PREPARE_RESPONSE;
  }

  public List<ProbeDescriptor> getClientProbes() {
    return clientProbes;
  }

  public void setClientProbes(List<ProbeDescriptor> clientProbes) {
    this.clientProbes = clientProbes;
  }
}

