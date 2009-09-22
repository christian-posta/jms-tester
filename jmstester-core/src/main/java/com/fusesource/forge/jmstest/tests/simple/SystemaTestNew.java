/**
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
package com.fusesource.forge.jmstest.tests.simple;

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeClass;

import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.impl.DynamicJMSConnectionProvider;

@ContextConfiguration(locations = {
  "classpath:com/fusesource/forge/jmstest/tests/simple/systema-bsf.xml",
  "classpath:com/fusesource/forge/jmstest/tests/simple/systema-beans.xml"
})
public class SystemaTestNew extends AbstractNetworkOfBrokersTest {

  private static final int WAIT_TIME = 10000;
  
  @BeforeClass
  public void waitForBrokers() {
    try {
      Thread.sleep(WAIT_TIME);
    } catch (InterruptedException ie) {
      // ignore
    }
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

