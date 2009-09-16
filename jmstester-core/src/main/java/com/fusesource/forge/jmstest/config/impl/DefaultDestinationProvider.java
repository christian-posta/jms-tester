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

/**
 * Provides creation of JMS Destination from JMS Session.
 * 
 * @author Andreas Gies
 * 
 */
public class DefaultDestinationProvider extends AbstractJMSDestinationProvider {

  /**
   * Creates JMS destination from JMS session. Creates either topic or queue based on objectType property
   * 
   * @param _session
   *          JMS Session
   * @param _dest
   *          Name of the JMS destination
   * @return JMS destination
   * @throws XQException
   *           thrown when object creation fails
   * 
   * @see com.fusesource.forge.jmstest.config.JMSDestinationProvider.esbx.utils.jms.IJMSDestinationProvider#getDestination(javax.jms.Session, java.lang.String)
   */
  public final Destination getDestination(Session session, String destName) throws Exception {
	  
	  int pos = destName.indexOf(":");

	  String objType = "queue";
	  
	  if (pos >= 0) {
		  objType = destName.substring(0, pos);
		  destName = destName.substring(pos+1);
	  }
	  
      if (objType.equalsIgnoreCase("topic")) {
        return session.createTopic(destName);
      } else {
        return session.createQueue(destName);
      }
  }
}
