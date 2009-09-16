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

import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import com.fusesource.forge.jmstest.config.JMSDestinationProvider;


/**
 * Implements getQueue and getTopic. Method getDestination must still be 
 * implemented by child class.
 * @author Andreas Gies
 * 
 */
public abstract class AbstractJMSDestinationProvider implements JMSDestinationProvider {
  /**
   * @param _session JMS Session object
   * @param _queue JMS destination name
   * @return JMS Queue
   * @throws XQException error during this operation
   *
   * @see com.fusesource.forge.jmstest.config.JMSDestinationProvider.esbx.utils.jms.IJMSDestinationProvider#getQueue(javax.jms.Session, java.lang.String)
   */
  public final Queue getQueue(final Session _session, final String _queue) throws Exception {
    return (Queue) getDestination(_session, _queue);
  }

  /**
   * @param _session JMS Session object
   * @param _topic JMS destination name
   * @return JMS Topic
   * @throws XQException error during this operation
   *
   * @see com.fusesource.forge.jmstest.config.JMSDestinationProvider.esbx.utils.jms.IJMSDestinationProvider#getTopic(javax.jms.Session, java.lang.String)
   */
  public final Topic getTopic(final Session _session, final String _topic) throws Exception {
    return (Topic) getDestination(_session, _topic);
  }
}
