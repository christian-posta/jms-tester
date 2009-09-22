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
package com.fusesource.forge.jmstest.frontend;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.fusesource.forge.jmstest.benchmark.command.handler.BenchmarkCommandChainHandler;
import com.fusesource.forge.jmstest.benchmark.command.handler.BenchmarkCommandHandler;
import com.fusesource.forge.jmstest.benchmark.command.transport.CommandTransport;
import com.fusesource.forge.jmstest.benchmark.command.transport.JMSCommandTransport;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultDestinationProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultJMSConnectionProvider;

public class BenchmarkConnector {

  private CommandTransport cmdTransport = null;

  private JMSConnectionProvider jmsConnectionProvider = null;
  private JMSDestinationProvider jmsDestinationProvider = null;
  private String destinationName = "topic:benchmark.command";

  private String hostName = null;;
  private int port = 62626;

  private BenchmarkCommandChainHandler handler = new BenchmarkCommandChainHandler();

  public String getHostname() {
    if (hostName == null) {
      try {
        hostName = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
        hostName = "localhost";
      }
    }
    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public int getPort() {
    return port;
  }

  public String getBrokerUrl() {
    return "tcp://" + getHostname() + ":" + getPort();
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getDestinationName() {
    return destinationName;
  }

  public void setDestinationName(String destinationName) {
    this.destinationName = destinationName;
  }

  public JMSConnectionProvider getJmsConnectionProvider() {
    if (jmsConnectionProvider == null) {
      jmsConnectionProvider = new DefaultJMSConnectionProvider();
      ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
      cf.setBrokerURL(getBrokerUrl());
      ((DefaultJMSConnectionProvider) jmsConnectionProvider)
          .setConnectionFactory(cf);
    }
    return jmsConnectionProvider;
  }

  public JMSDestinationProvider getJmsDestinationProvider() {
    if (jmsDestinationProvider == null) {
      jmsDestinationProvider = new DefaultDestinationProvider();
    }
    return jmsDestinationProvider;
  }

  public void addHandler(BenchmarkCommandHandler handler) {
    this.handler.addHandler(handler);
  }

  public CommandTransport getCmdTransport() {
    if (cmdTransport == null) {
      cmdTransport = new JMSCommandTransport();
      ((JMSCommandTransport) cmdTransport)
          .setJmsConnectionProvider(getJmsConnectionProvider());
      ((JMSCommandTransport) cmdTransport)
          .setJmsDestinationProvider(getJmsDestinationProvider());
      ((JMSCommandTransport) cmdTransport)
          .setDestinationName(getDestinationName());
      cmdTransport.setHandler(handler);
      cmdTransport.start();
    }
    return cmdTransport;
  }
}
