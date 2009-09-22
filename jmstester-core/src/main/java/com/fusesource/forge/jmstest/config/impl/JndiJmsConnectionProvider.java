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
import javax.naming.Context;

import com.fusesource.forge.jmstest.config.JndiInitialContextFactory;

/**
 * Gets JMS connections from configured JNDI store.
 *
 * @author Andreas Gies
 */
public class JndiJmsConnectionProvider extends DefaultJMSConnectionProvider {

  private JndiInitialContextFactory jndiCtxtFactory = null;
  private String connectionFactoryName_ = "ConnectionFactory";

  @Override
  public ConnectionFactory getConnectionFactory() throws Exception {
    Context ctxt = getJndiInitialContextFactory().getInitialContext();
    ConnectionFactory factory = (ConnectionFactory) ctxt
        .lookup(getConnectionFactoryName());
    return factory;
  }

  public final void setConnectionFactoryName(final String _connectionFactoryName) {
    connectionFactoryName_ = _connectionFactoryName;
  }

  public String getConnectionFactoryName() {
    return connectionFactoryName_;
  }

  public final void setJndiInitialContextFactory(
      final JndiInitialContextFactory factory) {
    jndiCtxtFactory = factory;
  }

  public JndiInitialContextFactory getJndiInitialContextFactory() {
    return jndiCtxtFactory;
  }
}
