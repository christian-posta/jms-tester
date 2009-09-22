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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.broker.util.LoggingBrokerPlugin;
import org.apache.activemq.broker.util.TraceBrokerPathPlugin;
import org.apache.activemq.network.NetworkConnector;

public class DynamicBrokerServicesFactory extends AbstractBrokerServicesFactory {

    private static final char[] BROKER_TAGS = new char[] {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
      };

  private int numBrokers = 1;
  private String brokerBaseName = "Broker";
  private String discoveryUri = null;
  private int networkTTL = 1;
  private int networkPrefetchSize = 1000;
  private boolean useJMX = true;
  private int jmxBasePort = 1099;
  private boolean decreaseNetworkConsumerPriority = false;

  private Map<String, BrokerService> brokerServices = null;

  protected BrokerService createBroker(int index) throws Exception {

    String brokerName = getBrokerBaseName() + BROKER_TAGS[index];

    BrokerService broker = new BrokerService();
    broker.setBrokerName(brokerName);
    broker.setDedicatedTaskRunner(false);
    TransportConnector tc = broker.addConnector("tcp://localhost:0");
    if (getDiscoveryUri() != null) {
      URI uri = new URI(getDiscoveryUri());
      tc.setDiscoveryUri(uri);
      NetworkConnector nc = broker.addNetworkConnector(uri);
      nc.setDynamicOnly(true);
      nc.setNetworkTTL(getNetworkTTL());
      nc.setPrefetchSize(getNetworkPrefetchSize());
      nc
          .setDecreaseNetworkConsumerPriority(isDecreaseNetworkConsumerPriority());
    }
    broker.setUseJmx(isUseJMX());
    if (isUseJMX()) {
      ManagementContext mc = new ManagementContext();
      mc.setConnectorPort(getJmxBasePort() + index);
      mc.setCreateConnector(true);
      broker.setManagementContext(mc);
    }
    broker.setUseLoggingForShutdownErrors(false);
    broker.setDeleteAllMessagesOnStartup(true);

    BrokerPlugin stampingPlugin = new TraceBrokerPathPlugin();

    LoggingBrokerPlugin loggingPlugin = new LoggingBrokerPlugin();
    loggingPlugin.setLogConsumerEvents(true);
    loggingPlugin.setLogAll(false);

    broker.setPlugins(new BrokerPlugin[] {stampingPlugin, loggingPlugin });

    broker.start();
    return broker;
  }

  public Map<String, BrokerService> getBrokerServices() throws Exception {

    if (brokerServices == null) {
      brokerServices = new HashMap<String, BrokerService>();
      for (int i = 0; i < getNumBrokers(); i++) {
        BrokerService broker = createBroker(i);
        brokerServices.put(broker.getBrokerName(), broker);
      }
    }
    return brokerServices;
  }

  public int getNetworkPrefetchSize() {
    return networkPrefetchSize;
  }

  public void setNetworkPrefetchSize(int networkPrefetchSize) {
    this.networkPrefetchSize = networkPrefetchSize;
  }

  public int getNumBrokers() {
    return numBrokers;
  }

  public void setNumBrokers(int numBrokers) {
    this.numBrokers = numBrokers;
  }

  public String getBrokerBaseName() {
    return brokerBaseName;
  }

  public void setBrokerBaseName(String brokerBaseName) {
    this.brokerBaseName = brokerBaseName;
  }

  public String getDiscoveryUri() {
    return discoveryUri;
  }

  public void setDiscoveryUri(String discoveryUri) {
    this.discoveryUri = discoveryUri;
  }

  public int getNetworkTTL() {
    return networkTTL;
  }

  public void setNetworkTTL(int networkTTL) {
    this.networkTTL = networkTTL;
  }

  public boolean isUseJMX() {
    return useJMX;
  }

  public void setUseJMX(boolean useJMX) {
    this.useJMX = useJMX;
  }

  public void setBrokerServices(Map<String, BrokerService> brokerServices) {
    this.brokerServices = brokerServices;
  }

  public int getJmxBasePort() {
    return jmxBasePort;
  }

  public void setJmxBasePort(int jmxBasePort) {
    this.jmxBasePort = jmxBasePort;
  }

  public boolean isDecreaseNetworkConsumerPriority() {
    return decreaseNetworkConsumerPriority;
  }

  public void setDecreaseNetworkConsumerPriority(
      boolean decreaseNetworkConsumerPriority) {
    this.decreaseNetworkConsumerPriority = decreaseNetworkConsumerPriority;
  }
}
