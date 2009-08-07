/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class DefaultMessageFactory implements MessageFactory {
	
	private String prefix = null;
	private int msgSize = 1000;
	
	public void setMessageSize(int msgSize) {
		this.msgSize = msgSize;
	}
	
	public int getMessageSize() {
		return msgSize;
	}
	
    public void setPrefix(String prefix) {
        this.prefix = prefix;	
    }
    
	public String getPrefix() {
		return prefix!=null?prefix:"";
	}
	
    protected String getMessageText() {
    	
        StringBuffer buffer = new StringBuffer(getMessageSize());
        buffer.append(getPrefix());
        buffer.append(getAdditionalContent());
        if (buffer.length() > getMessageSize()) {
            return buffer.substring(0, getMessageSize());
        }
        for (int i = buffer.length(); i < getMessageSize(); i++) {
            buffer.append(' ');
        }
        return buffer.toString();
    }

    protected String getAdditionalContent() {
        return "";	
    }
    
    public Message createMessage(Session session) throws JMSException {
    	return session.createTextMessage(getMessageText());
	}
}
