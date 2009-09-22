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

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBrokerServicesFactory extends AbstractBrokerServicesFactory
    implements ApplicationContextAware {

  private transient Log log = null;

  private Map<String, BrokerService> brokerServices = null;
  private ApplicationContext ac = null;

  public Map<String, BrokerService> getBrokerServices() {
    if (brokerServices == null) {
      brokerServices = new HashMap<String, BrokerService>();

      for (String name : ac.getBeanNamesForType(BrokerService.class)) {
        BrokerService broker = (BrokerService) ac.getBean(name);
        log().info(
            "Found broker definition for broker : " + broker.getBrokerName());
        brokerServices.put(broker.getBrokerName(), broker);
      }
    }
    return brokerServices;
  }

  public void setApplicationContext(ApplicationContext ac)
      throws BeansException {
    this.ac = ac;
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
