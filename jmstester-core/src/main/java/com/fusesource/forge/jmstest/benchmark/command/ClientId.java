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

import java.io.Serializable;

public class ClientId implements Serializable {

  private static final long serialVersionUID = 36768397997494584L;

  private ClientType clientType;
  private String clientName;
  private String benchmarkId;
  private String partId;

  public ClientId(
    ClientType clientType, String clientName, 
    String benchmarkId, String partId
  ) {

    this.clientType = clientType;
    this.clientName = clientName;
    this.benchmarkId = benchmarkId;
    this.partId = partId;
  }

  public ClientType getClientType() {
    return clientType;
  }

  public void setClientType(ClientType clientType) {
    this.clientType = clientType;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getBenchmarkId() {
    return benchmarkId;
  }

  public void setBenchmarkId(String benchmarkId) {
    this.benchmarkId = benchmarkId;
  }

  public String getPartId() {
    return partId;
  }

  public void setPartId(String partId) {
    this.partId = partId;
  }

  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append(getClientType());
    buf.append("-");
    buf.append(getClientName());
    buf.append("-");
    buf.append(getBenchmarkId());
    buf.append("-");
    buf.append(getPartId());
    return buf.toString();
  }
}
