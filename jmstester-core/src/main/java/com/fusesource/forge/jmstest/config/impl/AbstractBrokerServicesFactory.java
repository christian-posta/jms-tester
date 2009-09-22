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

import java.util.Iterator;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.config.BrokerServicesFactory;

/**
 * Provide a base class implementing the BrokerServiceFactory. All methods
 * except getBrokerServices can be implemented by this class.
 */
public abstract class AbstractBrokerServicesFactory implements
    BrokerServicesFactory {

  private static final Log LOG = LogFactory
      .getLog(AbstractBrokerServicesFactory.class);

  public BrokerService getBroker(String brokerName) throws Exception {
    return getBrokerServices().get(brokerName);
  }

  public void start(String brokerName) throws Exception {
    LOG.trace("Trying to start Broker : " + brokerName);
    BrokerService broker = getBrokerServices().get(brokerName);
    if (broker == null) {
      throw new Exception("No such broker : " + brokerName);
    }
    broker.start();
  }

  public void stop(String brokerName) throws Exception {
    LOG.trace("Trying to stop Broker : " + brokerName);
    BrokerService broker = getBrokerServices().get(brokerName);
    if (broker == null) {
      throw new Exception("No such broker : " + brokerName);
    }
    broker.stop();
    broker.waitUntilStopped();
  }

  @SuppressWarnings("unchecked")
  public void startAll() throws Exception {
    LOG.trace("Trying to start Message brokers");
    if (getBrokerServices() != null) {
      for (Iterator it = getBrokerServices().keySet().iterator(); it.hasNext();) {
        String brokerName = (String) it.next();
        start(brokerName);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void stopAll() throws Exception {
    LOG.trace("Trying to stop Message brokers");
    if (getBrokerServices() != null) {
      for (Iterator it = getBrokerServices().keySet().iterator(); it.hasNext();) {
        String brokerName = (String) it.next();
        stop(brokerName);
      }
    }
  }
}
