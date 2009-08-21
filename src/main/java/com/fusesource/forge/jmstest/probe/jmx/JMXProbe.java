package com.fusesource.forge.jmstest.probe.jmx;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.probe.AbstractProbe;

public class JMXProbe extends AbstractProbe {
	
	private JMXConnectionFactory jmxConnFactory;
	private ObjectName objectName;
	private String attributeName;
	
	private boolean active = true;
	
	private MBeanServerConnection jmxConnection;
	private Log log = null;
	
	@Override
	public Number getValue() {
		if (!isActive()) {
			log().warn("JMXProbe is not accessible...value might not make sense.");
			return Double.MIN_VALUE;
		}
		
		Object attribute = null;
		try {
			attribute = getJMXConnection().getAttribute(getObjectName(), getAttributeName()); 
		} catch (Exception e) {
			log().error("Error retrieving Attribute for: " + getObjectName().toString() + "[" + getAttributeName() + "]", e);
			setActive(false);
			return Double.MIN_VALUE;
		}
		
		if (attribute instanceof Number) {
			return (Number)attribute;
		} else {
			log.error("Attribute:" + getObjectName().toString() + "[" + getAttributeName() + "] does not represent a number");
			setActive(false);
			return Double.MIN_VALUE;
		}
	}
	
	public JMXConnectionFactory getJmxConnectionFactory() {
		return jmxConnFactory;
	}

	public void setJmxConnectionFactory(JMXConnectionFactory jmxConnFactory) {
		this.jmxConnFactory = jmxConnFactory;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ObjectName getObjectName() {
		return objectName;
	}
	
	public String getObjectNameString() {
		return objectName.toString();
	}

	public void setObjectNameString(String objectName) {
		try {
			this.objectName = new ObjectName(objectName);
		} catch (Exception e) {
			log().error("Error seting ObjectName for Probe: " + objectName);
			active = false;
		}
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	private MBeanServerConnection getJMXConnection() {
		if (jmxConnection == null) {
			try {
				jmxConnection = getJmxConnectionFactory().getConnector().getMBeanServerConnection();
			} catch (IOException ioe) {
				log().error("Error establishing JMX connection.", ioe);
			}
		}
		return jmxConnection;
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}

}
