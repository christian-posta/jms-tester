package com.fusesource.forge.jmstest.probe.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JMXConnectionFactory {
	
	private JMXConnector connector = null;
	private String username = null;
	private String password = null;
	private String url = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
	
	private Log log = null;
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return this.url;
	}

	public JMXConnector getConnector() {
		
		JMXConnector connector = null;
		
		try {
			if (connector == null) {
		         if(username != null && password != null) {
		        	 Map<String, String[]> m = new HashMap<String,String[]>();
		        	 m.put(JMXConnector.CREDENTIALS,new String[] {username,password});
		        	 connector = JMXConnectorFactory.connect(new JMXServiceURL(url), m);
		         } else {
		        	 connector = JMXConnectorFactory.connect(new JMXServiceURL(url));
		         }
			}
		} catch (IOException ioe) {
			//TODO: do this right
			log().error("Error connecting to JMX server ", ioe);
		}
		return connector;
	}
	
	public void close() {
		try {
			if (connector != null) {
				connector.close();
			}
		} catch (IOException ioe) {
			
		}
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
