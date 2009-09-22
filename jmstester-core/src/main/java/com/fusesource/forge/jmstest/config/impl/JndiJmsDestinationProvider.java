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

import javax.jms.Destination;
import javax.jms.Session;
import javax.naming.Context;

import com.fusesource.forge.jmstest.config.JndiInitialContextFactory;

/**
 * Provides lookup of JMS Destination in JNDI store.
 *
 * @author Andreas Gies
 */
public class JndiJmsDestinationProvider extends AbstractJMSDestinationProvider {

  private JndiInitialContextFactory jndiCtxtFactory = null;

  /**
   * Lookup JMS destination in JNDI store.
   *
   * @param session
   *          not used in this method
   * @param dest
   *          Lookup(Logical) name of the destination in JNDI
   * @return JMS destination
   * @throws Exception
   *           thrown when JNDI lookup fails
   * @see com.fusesource.forge.jmstest.config.JMSDestinationProvider.esbx.utils.jms.IJMSDestinationProvider#getDestination(javax.jms.Session,
   *      java.lang.String)
   */
  public final Destination getDestination(
    final Session session,
    final String dest
  ) throws Exception {
    final Context ctxt = getJndiInitialContextFactory().getInitialContext();
    final Destination destination = (Destination) ctxt.lookup(dest);
    return destination;
  }

  /**
   * @param factory
   *          JndiInitialContextFactory Object
   */
  public final void setJndiInitialContextFactory(
    final JndiInitialContextFactory factory) {
    jndiCtxtFactory = factory;
  }

  private JndiInitialContextFactory getJndiInitialContextFactory() {
    return jndiCtxtFactory;

  }
}
