package com.fusesource.forge.jmstest.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.fusesource.forge.jmstest.config.BrokerServicesFactory;

public class JMSTest extends AbstractTestNGSpringContextTests {

	private transient Log log = null; 

	protected Object getBeanByClass(Class clazz) {
		Object result = null;
		String [] beanNames = applicationContext.getBeanNamesForType(clazz);
		if (beanNames != null && beanNames.length > 0) {
			log().debug("Using bean (" + beanNames[0] + " for type: " + clazz.getName());
			result = applicationContext.getBean(beanNames[0]);
		}
		if (beanNames.length > 1) {
			log().warn("Found " + beanNames.length + " beans for type " + clazz.getName());
		}
		return result;
	}
	
	public BrokerServicesFactory getBrokerServicesFactory() {
		return (BrokerServicesFactory)getBeanByClass(BrokerServicesFactory.class);
	}

	@BeforeClass
	public void setUp() {
		try {
			log().info("Initializing Test ...");
			BrokerServicesFactory bsf = getBrokerServicesFactory();
			if (bsf == null) {
				log().warn("No BrokerServicesFactory configured in Test ...");
			} else {
			    bsf.startAll();
			}
		} catch (Exception e) {
			Assert.fail("Unexpected exception setting up test", e);
		}
	}
	
	@AfterClass
	public void tearDown() {
		try {
			log().info("Test finished ...");
			BrokerServicesFactory bsf = getBrokerServicesFactory();
			if (bsf == null) {
				log().warn("No Brokers to shut down.");
			} else {
				getBrokerServicesFactory().stopAll();
			}
		} catch (Exception e) {
			Assert.fail("Unexpected exception cleaning up test", e);
		}
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
