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

import org.springframework.beans.factory.InitializingBean;

public class AMQDestinationProbe extends JMXProbe implements InitializingBean {

  private String destinationName = null;
  private String destinationType = "Queue";
  private String brokerName = "localhost";
  private String objectNamePrefix = null;

  public AMQDestinationProbe() {
    super();
  }

  public AMQDestinationProbe(String destinationName) {
    super();
    this.destinationName = destinationName;
  }

  public String getBrokerName() {
    return brokerName;
  }

  public void setBrokerName(String brokerName) {
    this.brokerName = brokerName;
  }

  public String getDestinationName() {
    return destinationName;
  }

  public void setDestinationName(String destinationName) {
    this.destinationName = destinationName;
  }

  public String getDestinationType() {
    return destinationType;
  }

  public void setDestinationType(String destinationType) {
    this.destinationType = destinationType;
  }

  public void afterPropertiesSet() throws Exception {
    setObjectNameString(getObjectNamePrefix() + getDestinationName());
    setName(getDestinationType() + ":" + getDestinationName() + "-"
        + getAttributeName());
  }

  private String getObjectNamePrefix() {
    if (objectNamePrefix == null) {
      objectNamePrefix = "org.apache.activemq:BrokerName=" + getBrokerName()
          + ",Type=" + getDestinationType() + ",Destination=";
    }
    return objectNamePrefix;
  }
}
