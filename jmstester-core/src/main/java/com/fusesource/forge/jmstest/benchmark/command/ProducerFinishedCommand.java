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

import com.fusesource.forge.jmstest.executor.AbstractBenchmarkClient;

public class ProducerFinishedCommand extends StartBenchmarkCommand {

  private static final long serialVersionUID = -5192978795630743148L;

  private ClientId clientId;

  public ProducerFinishedCommand(AbstractBenchmarkClient client) {
    super(client.getConfig().getBenchmarkId());
    this.clientId = client.getClientId();
  }

  @Override
  public byte getCommandType() {
    return CommandTypes.PRODUCER_FINISHED;
  }

  public ClientId getClientId() {
    return clientId;
  }

  public void setClientId(ClientId clientId) {
    this.clientId = clientId;
  }
}
