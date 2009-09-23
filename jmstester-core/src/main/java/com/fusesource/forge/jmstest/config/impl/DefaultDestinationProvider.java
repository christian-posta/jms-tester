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

/**
 * Provides creation of JMS Destination from JMS Session.
 *
 * @author Andreas Gies
 *
 */
public class DefaultDestinationProvider extends AbstractJMSDestinationProvider {

  /**
   * Creates JMS destination from JMS session. Creates either topic or queue
   * based on objectType property
   *
   * @param session
   *          JMS Session
   * @param destName
   *          Name of the JMS destination
   * @return JMS destination
   * @throws Exception
   *           thrown when object creation fails
   *
   * @see com.fusesource.forge.jmstest.config.JMSDestinationProvider.esbx.utils.jms.IJMSDestinationProvider#getDestination(javax.jms.Session,
   *      java.lang.String)
   */
  public final Destination getDestination(final Session session, final String destName)
    throws Exception {

    String destination = destName;
    int pos = destination.indexOf(":");
    String objType = "queue";

    if (pos >= 0) {
      objType = destination.substring(0, pos);
      destination = destination.substring(pos + 1);
    }

    if (objType.equalsIgnoreCase("topic")) {
      return session.createTopic(destination);
    } else {
      return session.createQueue(destination);
    }
  }
}
