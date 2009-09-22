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
package com.fusesource.forge.jmstest.config.impl;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;

import com.fusesource.forge.jmstest.config.BrokerServicesFactory;

public class DynamicJMSConnectionProvider extends DefaultJMSConnectionProvider {

  private BrokerServicesFactory brokerServicesFactory;
  private String selectedBroker;

  protected ConnectionFactory createConnectionFactory(String brokerName)
    throws Exception {

    BrokerService broker = getBrokerServicesFactory().getBroker(brokerName);
    if (broker == null) {
      throw new Exception("Broker (" + brokerName + ") does not exist.");
    }
    String url = ((TransportConnector) broker.getTransportConnectors().get(0))
        .getServer().getConnectURI().toString();
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
        url);
    ActiveMQPrefetchPolicy qPrefetchPolicy = new ActiveMQPrefetchPolicy();
    qPrefetchPolicy.setQueuePrefetch(1);
    connectionFactory.setPrefetchPolicy(qPrefetchPolicy);
    return connectionFactory;
  }

  public BrokerServicesFactory getBrokerServicesFactory() {
    return brokerServicesFactory;
  }

  public void setBrokerServicesFactory(
      BrokerServicesFactory brokerServicesFactory) {
    this.brokerServicesFactory = brokerServicesFactory;
  }

  public String getSelectedBroker() {
    return selectedBroker;
  }

  public void setSelectedBroker(String selectedBroker) {
    try {
      setConnectionFactory(createConnectionFactory(selectedBroker));
      this.selectedBroker = selectedBroker;
    } catch (Exception e) {
      // TODO: Handle this properly
    }
  }
}
