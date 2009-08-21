package com.fusesource.forge.jmstest.executor;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;

public abstract class AbstractJMSClientComponent implements Releaseable {

	private BenchmarkClientWrapper container = null;
	private BenchmarkRunStatus benchmarkStatus;

	private Connection conn;
    private Session session;
    
    private Log log = null;

	public AbstractJMSClientComponent(BenchmarkClientWrapper container) {
		this.container = container;
	}
	
    public BenchmarkClientWrapper getContainer() {
		return container;
	}

	public BenchmarkRunStatus getBenchmarkStatus() {
        return benchmarkStatus;
    }

    public JMSConnectionProvider getConnectionProvider() {
    	return getContainer().getJmsConnectionProvider();
	}

	public JMSDestinationProvider getDestinationProvider() {
		return getContainer().getJmsDestinationProvider();
	}

    public Connection getConnection() throws Exception {
    	if (conn == null) {
   			conn = getConnectionProvider().getConnection();
		}
		return conn;
	}

	public Session getSession() throws Exception {
		if (session == null) {
			session = getConnection().createSession(
				getContainer().getConfig().isTransacted(), 
				getContainer().getConfig().getAcknowledgeMode().getCode()
			);
		}
		return session;
	}
	
	public void prepare() {
       ReleaseManager.getInstance().register(this);
	}
	
	public void release() {
       ReleaseManager.getInstance().deregister(this);
	   try {
            if (session != null) {
                session.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [session]", e);
        } finally {
        	session = null;
        }
        
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [connection]", e);
        } finally {
        	conn = null;
        }
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
