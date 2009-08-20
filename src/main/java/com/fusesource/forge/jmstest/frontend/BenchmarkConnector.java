package com.fusesource.forge.jmstest.frontend;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommandChainHandler;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommandHandler;
import com.fusesource.forge.jmstest.benchmark.command.CommandTransport;
import com.fusesource.forge.jmstest.benchmark.command.JMSCommandTransport;
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
			((DefaultJMSConnectionProvider)jmsConnectionProvider).setConnectionFactory(cf);
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
			((JMSCommandTransport)cmdTransport).setJmsConnectionProvider(getJmsConnectionProvider());
			((JMSCommandTransport)cmdTransport).setJmsDestinationProvider(getJmsDestinationProvider());
			((JMSCommandTransport)cmdTransport).setDestinationName(getDestinationName());
			cmdTransport.setHandler(handler);
			cmdTransport.start();
		}
		return cmdTransport;
	}
}
