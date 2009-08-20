/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.config;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

public interface JMSConnectionProvider {
	
  public final static String DEFAULT_BEAN_NAME = "connectionProvider";
	
  public ConnectionFactory getConnectionFactory() throws Exception;	
  public Connection getConnection() throws Exception;
  public TopicConnection getTopicConnection() throws Exception;
  public QueueConnection getQueueConnection() throws Exception;
}
