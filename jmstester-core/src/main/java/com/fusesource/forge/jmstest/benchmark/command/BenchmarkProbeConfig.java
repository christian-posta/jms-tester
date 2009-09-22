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
import java.util.HashMap;
import java.util.Map;

import com.fusesource.forge.jmstest.probe.jmx.JMXConnectionFactory;

public class BenchmarkProbeConfig implements Serializable {

  private static final long serialVersionUID = -7087505154273084776L;

  private Map<String, String> jmxConnectionFactoryNames = null;

  private String clientNames = "All";
  private String probeNames = "All";

  public String getProbeNames() {
    return probeNames;
  }

  public void setProbeNames(String probeNames) {
    this.probeNames = probeNames;
  }

  public String getClientNames() {
    return clientNames;
  }

  public void setClientNames(String clientNames) {
    this.clientNames = clientNames;
  }

  public Map<String, String> getJmxConnectionFactoryNames() {
    if (jmxConnectionFactoryNames == null) {
      jmxConnectionFactoryNames = new HashMap<String, String>();
    }
    return jmxConnectionFactoryNames;
  }

  public void setJmxConnectionFactoryNames(
      Map<String, String> jmxConnectionFactoryNames) {
    this.jmxConnectionFactoryNames = jmxConnectionFactoryNames;
  }

  public String getPreferredJmxConnectionFactoryName(String clientName) {

    for (String key : getJmxConnectionFactoryNames().keySet()) {
      if (clientName.matches(key)) {
        return getJmxConnectionFactoryNames().get(key);
      }
    }

    return JMXConnectionFactory.DEFAULT_JMX_CONNECTION_FACTORY_NAME;
  }
}
