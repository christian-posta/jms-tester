package com.fusesource.forge.jmstest.tests;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Session;

import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;

public abstract class AsyncClient {

	private JMSConnectionProvider connectionProvider = null;
    private JMSDestinationProvider destinationProvider = null;
    private String destinationName = "TEST";
    private String name ="JMSClient";
    
    private boolean started;
    
    private Connection con = null;
    
    synchronized public final void start() {
    	if (started) {
    		return;
    	}
    	doStart();
    	started =true;
    }
    
    synchronized public final void stop() {
    	if (!started) {
    		return;
    	}
    	doStop();
    	started = false;
    }
    
    public abstract void doStop();
    public abstract void doStart();
    
	public JMSConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}
	
	public void setConnectionProvider(JMSConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public JMSDestinationProvider getDestinationProvider() {
		return destinationProvider;
	}
	
	public void setDestinationProvider(JMSDestinationProvider destinationProvider) {
		this.destinationProvider = destinationProvider;
	}

    public String getDestinationName() {
    	return destinationName;
    }
    
    public void setDestinationName(final String name) {
    	destinationName = name;
    }
    
	protected Connection getConnection() throws Exception {
		if (con == null) {
			con = getConnectionProvider().getConnection();
		}
		return con;
	}
	
	protected void closeConnection() {
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {
				// TODO: Handle gracefully
			} finally {
				con = null;
			}
		}
	}
	
	protected Destination getDestination(Session session, String destName) throws Exception {
		return getDestinationProvider().getDestination(session, destName);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
} 
