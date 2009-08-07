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
package com.fusesource.forge.jmstest.tests.simple;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.test.context.ContextConfiguration;

import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultJMSConnectionProvider;

@ContextConfiguration(locations={
		"classpath:com/fusesource/forge/jmstest/tests/simple/systema-ext.xml",
		"classpath:com/fusesource/forge/jmstest/tests/simple/systema-beans.xml"
})
public class SystemaTestExternalBrokers extends AbstractNetworkOfBrokersTest {


	@Override
	protected Map<String, JMSConnectionProvider> doGetConnectionProviders() {
		
		Map<String, JMSConnectionProvider> result = new HashMap<String, JMSConnectionProvider>();
		Properties connectorMap = (Properties)applicationContext.getBean("connectorMap");

		connectorMap.put("BrokerA", "tcp://localhost:61616");
		connectorMap.put("BrokerB", "tcp://localhost:61618");
		connectorMap.put("BrokerC", "tcp://localhost:61620");
		
		ObjectFactory cfCreator = (ObjectFactory)(applicationContext.getBean("connectionFactoryFactory"));
		
		for(Enumeration en = connectorMap.keys(); en.hasMoreElements(); ) {
			String key = (String)en.nextElement();
			String value = connectorMap.getProperty(key);
			JMSConnectionProvider provider = new DefaultJMSConnectionProvider();
			ConnectionFactory cf = (ConnectionFactory)cfCreator.getObject();
			((ActiveMQConnectionFactory)cf).setBrokerURL(value);
			((DefaultJMSConnectionProvider)provider).setConnectionFactory(cf);
			result.put(key, provider);
		}

		return result;
	}
}

