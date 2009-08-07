package com.fusesource.forge.jmstest.tests;

import javax.jms.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;

import com.fusesource.forge.jmstest.config.BrokerServicesFactory;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;

public abstract class AbstractJMSTest {

	private transient Log log = null;
	private ApplicationContext appCtxt = null;

	abstract protected String[] getConfigLocations();
			
	protected ApplicationContext getApplicationContext() {
        if (appCtxt == null) {
        	appCtxt = new ClassPathXmlApplicationContext(getConfigLocations());
        }
        return appCtxt;
	}
	
	protected Object getBeanByClass(Class clazz) {
		Object result = null;
		String [] beanNames = getApplicationContext().getBeanNamesForType(clazz);
		if (beanNames != null && beanNames.length > 0) {
			log().debug("Using bean (" + beanNames[0] + " for type: " + clazz.getName());
			result = getApplicationContext().getBean(beanNames[0]);
		}
		if (beanNames.length > 1) {
			log().warn("Found " + beanNames.length + " beans for type " + clazz.getName());
		}
		return result;
	}
	
	protected JMSConnectionProvider getConnectionProvider() {
		return (JMSConnectionProvider)getBeanByClass(JMSConnectionProvider.class);
	}
	
	protected JMSDestinationProvider getDestinationProvider() {
		JMSDestinationProvider destProvider = (JMSDestinationProvider)getBeanByClass(JMSDestinationProvider.class);
		return destProvider;
	}
	
	protected void startBrokers() throws Exception {
		BrokerServicesFactory bsf = (BrokerServicesFactory)getBeanByClass(BrokerServicesFactory.class);
		if (bsf != null) {
			bsf.startAll();
		}
	}
	
	protected void stopBrokers() throws Exception {
		BrokerServicesFactory bsf = (BrokerServicesFactory)getBeanByClass(BrokerServicesFactory.class);
		if (bsf != null) {
			bsf.stopAll();
		}
	}

	protected Connection getConnection() throws Exception {
		return getConnectionProvider().getConnection();
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
