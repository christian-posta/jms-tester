package com.fusesource.forge.jmstest.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public interface MessageFactory {

	public final static String DEFAULT_BEAN_NAME = "messageFactory";
	
	public Message createMessage(Session session) throws JMSException;
}
