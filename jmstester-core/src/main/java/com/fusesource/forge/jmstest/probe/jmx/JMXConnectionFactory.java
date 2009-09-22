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
package com.fusesource.forge.jmstest.probe.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JMXConnectionFactory {

  public static final String DEFAULT_JMX_CONNECTION_FACTORY_NAME = "jmxConnector";

  private JMXConnector connector = null;
  private String username = null;
  private String password = null;
  private String url = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";

  private Log log = null;

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return this.url;
  }

  public JMXConnector getConnector() {

    JMXConnector connector = null;

    try {
      if (connector == null) {
        if (username != null && password != null) {
          Map<String, String[]> m = new HashMap<String, String[]>();
          m.put(JMXConnector.CREDENTIALS, new String[] { username, password });
          connector = JMXConnectorFactory.connect(new JMXServiceURL(url), m);
        } else {
          connector = JMXConnectorFactory.connect(new JMXServiceURL(url));
        }
      }
    } catch (IOException ioe) {
      // TODO: do this right
      log().error("Error connecting to JMX server ", ioe);
    }
    return connector;
  }

  public void close() {
    try {
      if (connector != null) {
        connector.close();
      }
    } catch (IOException ioe) {

    }
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
