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

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeClass;

import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.impl.DynamicJMSConnectionProvider;

@ContextConfiguration(locations={
		"classpath:com/fusesource/forge/jmstest/tests/simple/systema-bsf.xml",
		"classpath:com/fusesource/forge/jmstest/tests/simple/systema-beans.xml"
})
public class SystemaTestNew extends AbstractNetworkOfBrokersTest {

	@BeforeClass
	public void waitForBrokers() {
		  try {
			  Thread.sleep(10000);
		  } catch (InterruptedException ie) {}
	}

	@Override
	protected Map<String, JMSConnectionProvider> doGetConnectionProviders() {

		Map<String, JMSConnectionProvider> result = new HashMap<String, JMSConnectionProvider>();

		try {
			Map<String, BrokerService> brokerServices = getBrokerServicesFactory().getBrokerServices();
			String[] brokerNames = brokerServices.keySet().toArray(new String[] {});
			ObjectFactory connectionProviderFactory = (ObjectFactory) (applicationContext.getBean("connectionProviderFactory"));
			for (String brokerName : brokerNames) {
				DynamicJMSConnectionProvider provider = (DynamicJMSConnectionProvider) connectionProviderFactory.getObject();
				provider.setSelectedBroker(brokerName);
				result.put(brokerName, provider);
			}
		} catch (Exception e) {
			// TODO: handle this correctly
		}

		return result;
	}
}

