package com.fusesource.forge.jmstest.tests.simple;

import org.springframework.beans.factory.ObjectFactory;

import com.fusesource.forge.jmstest.tests.AbstractJMSTest;
import com.fusesource.forge.jmstest.tests.AsyncProducer;

public class SimpleProducer extends AbstractJMSTest {

	private final static int NUM_PRODUCERS = 5;

	protected String[] getConfigLocations() {
		return new String[] {
//			"com/fusesource/forge/jmstest/tests/simple/broker-services.xml",
			"com/fusesource/forge/jmstest/tests/simple/test-beans.xml"
        };
	}
	
	protected void run() {
		ObjectFactory factory = (ObjectFactory)getApplicationContext().getBean("AsyncProducerFactory");
		
		for(int i=0; i<NUM_PRODUCERS; i++) {
			AsyncProducer producer = (AsyncProducer)factory.getObject();
			producer.setMsgGroup("MsgGroup-" + i);
			producer.start();
		}
	}

	public static void main(String [] args) {
		SimpleProducer bs = new SimpleProducer();
		bs.run();
	}

}
