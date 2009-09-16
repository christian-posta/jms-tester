/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
 * @author  Andreas Gies
 */
public class JndiJmsDestinationProvider extends AbstractJMSDestinationProvider {
  /**
 * @uml.property  name="jndiCtxtFactory_"
 * @uml.associationEnd  
 */
private JndiInitialContextFactory jndiCtxtFactory_ = null;

  /**
   * Lookup JMS destination in JNDI store.
   * 
   * @param _session
   *          not used in this method
   * @param _dest
   *          Lookup(Logical) name of the destination in JNDI
   * @return JMS destination
   * @throws Exception
   *           thrown when JNDI lookup fails
   * @see com.fusesource.forge.jmstest.config.JMSDestinationProvider.esbx.utils.jms.IJMSDestinationProvider#getDestination(javax.jms.Session, java.lang.String)
   */
  public final Destination getDestination(final Session _session, final String _dest) throws Exception {
      final Context ctxt = getJndiInitialContextFactory().getInitialContext();
      final Destination dest = (Destination) ctxt.lookup(_dest);
      return dest;
  }

  /**
   * @param _factory
   *          JndiInitialContextFactory Object
   */
  public final void setJndiInitialContextFactory(final JndiInitialContextFactory _factory) {
    jndiCtxtFactory_ = _factory;
  }

  private JndiInitialContextFactory getJndiInitialContextFactory() {
    return jndiCtxtFactory_;
  }
}
