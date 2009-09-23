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

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.probe.AbstractProbe;

public class JMXProbe extends AbstractProbe {

  private JMXConnectionFactory jmxConnFactory;
  private ObjectName objectName;
  private String attributeName;

  private MBeanServerConnection jmxConnection;
  private Log log = null;

  public JMXProbe() {
    super();
  }

  public JMXProbe(String objectName) {
    super();
    setObjectNameString(objectName);
  }

  public JMXProbe(String name, String objectName) {
    super(name);
    setObjectNameString(objectName);
  }

  @Override
  public Number getValue() throws Exception {
    if (!isActive()) {
      log().warn(
          "JMXProbe " + getName()
              + " is not accessible...value might not make sense.");
      return 0.0;
    }

    Object attribute = null;
    try {
      attribute = getJMXConnection().getAttribute(getObjectName(),
          getAttributeName());
    } catch (Exception e) {
      log().error(
          "Error retrieving Attribute for: " + getObjectName().toString() + "["
              + getAttributeName() + "]", e);
      throw(e);
    }

    if (attribute instanceof Number) {
      return (Number) attribute;
    } else {
      log.warn("Attribute:" + getObjectName().toString() + "["
          + getAttributeName() + "] does not represent a number");
      setActive(false);
      return 0.0;
    }
  }

  public JMXConnectionFactory getJmxConnectionFactory() {
    return jmxConnFactory;
  }

  public void setJmxConnectionFactory(JMXConnectionFactory jmxConnFactory) {
    this.jmxConnFactory = jmxConnFactory;
  }

  public ObjectName getObjectName() {
    return objectName;
  }

  public String getObjectNameString() {
    return objectName.toString();
  }

  public void setObjectNameString(String objectName) {
    try {
      this.objectName = new ObjectName(objectName);
    } catch (Exception e) {
      log().error("Error seting ObjectName for Probe: " + objectName);
      setActive(false);
      lastException = Long.MAX_VALUE;
    }
  }

  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  private MBeanServerConnection getJMXConnection() {
    if (jmxConnection == null) {
      try {
        jmxConnection = getJmxConnectionFactory().getConnector()
            .getMBeanServerConnection();
      } catch (IOException ioe) {
        log().error("Error establishing JMX connection.", ioe);
      }
    }
    return jmxConnection;
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }

}
