/*
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
package com.fusesource.forge.jmstest.config;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

public interface JMSConnectionProvider {

  public final static String DEFAULT_CONNECTION_FACTORY_NAME = "connectionFactory";

  public ConnectionFactory getConnectionFactory() throws Exception;

  public Connection getConnection() throws Exception;

  public TopicConnection getTopicConnection() throws Exception;

  public QueueConnection getQueueConnection() throws Exception;
}
