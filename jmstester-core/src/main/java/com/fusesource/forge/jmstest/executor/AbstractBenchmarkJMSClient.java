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
package com.fusesource.forge.jmstest.executor;

import javax.jms.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultDestinationProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultJMSConnectionProvider;
import com.fusesource.forge.jmstest.message.DefaultMessageFactory;
import com.fusesource.forge.jmstest.message.MessageFactory;
import com.fusesource.forge.jmstest.probe.ProbeRunner;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public abstract class AbstractBenchmarkJMSClient extends
    AbstractBenchmarkClient {

  private BenchmarkPartConfig partConfig;

  private JMSConnectionProvider jmsConnectionProvider = null;
  private JMSDestinationProvider jmsDestinationProvider = null;

  private ClientId clientId = null;

  private MessageFactory messageFactory = null;

  private Log log = null;

  public AbstractBenchmarkJMSClient(
    BenchmarkClient container,
    BenchmarkPartConfig partConfig
  ) {
    super(container, partConfig.getParent());
    this.partConfig = partConfig;
  }

  public BenchmarkPartConfig getPartConfig() {
    return partConfig;
  }

  @Override
  protected ProbeRunner getProbeRunner() {
    ProbeRunner runner = super.getProbeRunner();

    BenchmarkIteration iteration = getIteration(getPartConfig()
        .getProfileName());
    runner.setDuration(iteration.getTotalDuration());

    return runner;
  }

  public MessageFactory getMessageFactory() {
    if (messageFactory == null) {
      messageFactory = (MessageFactory) getBean(new String[] {
          getPartConfig().getMessageFactoryName(),
          MessageFactory.DEFAULT_BEAN_NAME }, MessageFactory.class);
    }
    if (messageFactory == null) {
      log().warn(
          "Could not resolve message factory object. Creating Default ...");
      messageFactory = new DefaultMessageFactory();
      ((DefaultMessageFactory) messageFactory).setPrefix(getClientId()
          .toString());
    }
    return messageFactory;
  }

  public abstract ClientType getClientType();

  public ClientId getClientId() {

    if (clientId == null) {
      clientId = new ClientId(getClientType(), getContainer().getClientInfo()
          .getClientName(), getConfig().getBenchmarkId(), getPartConfig()
          .getPartID());
    }
    return clientId;
  }

  public String getPreferredConnectionFactoryName() {

    String result = null;

    for (String key : getPartConfig().getConnectionFactoryNames().keySet()) {
      if (getContainer().getClientInfo().getClientName().matches(key)) {
        result = getPartConfig().getConnectionFactoryNames().get(key);
        break;
      }
    }

    if (result == null) {
      result = JMSConnectionProvider.DEFAULT_CONNECTION_FACTORY_NAME + "-"
          + getContainer().getClientInfo().getClientName();
    }

    log().debug("Preferred JMS Connection Factory Name is : " + result);

    return result;
  }

  protected JMSConnectionProvider getJmsConnectionProvider() {
    if (jmsConnectionProvider == null) {
      log().warn("Creating default JMS Connection Provider.");
      ConnectionFactory cf = (ConnectionFactory) getBean(new String[] {
          getPreferredConnectionFactoryName(),
          JMSConnectionProvider.DEFAULT_CONNECTION_FACTORY_NAME + "-"
              + getContainer().getClientInfo().getClientName(),
          JMSConnectionProvider.DEFAULT_CONNECTION_FACTORY_NAME },
          ConnectionFactory.class);
      if (cf != null) {
        jmsConnectionProvider = new DefaultJMSConnectionProvider();
        ((DefaultJMSConnectionProvider) jmsConnectionProvider)
            .setConnectionFactory(cf);
      }
    }
    return jmsConnectionProvider;
  }

  protected JMSDestinationProvider getJmsDestinationProvider() {
    if (jmsDestinationProvider == null) {
      jmsDestinationProvider = (JMSDestinationProvider) getBean(new String[] {
          partConfig.getJmsDestinationProviderName(),
          JMSDestinationProvider.DEFAULT_BEAN_NAME },
          JMSDestinationProvider.class);
    }
    if (jmsDestinationProvider == null) {
      log().warn("Creating Default Destination Provider.");
      jmsDestinationProvider = new DefaultDestinationProvider();
    }
    return jmsDestinationProvider;
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
