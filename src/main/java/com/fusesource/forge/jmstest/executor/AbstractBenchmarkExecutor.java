package com.fusesource.forge.jmstest.executor;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.transport.CommandTransport;
import com.fusesource.forge.jmstest.config.SpringConfigHelper;
import com.fusesource.forge.jmstest.frontend.BenchmarkConnector;

public abstract class AbstractBenchmarkExecutor {

	private BenchmarkConnector benchmarkConnector = new BenchmarkConnector();
	private Log log = null;
	private SpringConfigHelper springConfigHelper = new SpringConfigHelper();
	
	
	protected BenchmarkConnector getConnector() {
		return benchmarkConnector;
	}
	
	public CommandTransport getCmdTransport() {
		return benchmarkConnector.getCmdTransport();
	}
	
	public final void sendCommand(BenchmarkCommand command) {
		getCmdTransport().sendCommand(command);
	}
	
	public void setJmsPort(int jmsPort) {
		getConnector().setPort(jmsPort);
	}

	public int getJmsPort() {
		return getConnector().getPort();
	}
	
	public void setHostname(String hostname) {
		getConnector().setHostName(hostname);
	}

	public void setDestinationName(String destinationName) {
		getConnector().setDestinationName(destinationName);
	}

	public void setSpringConfigLocations(String springConfigDirs) {
		this.springConfigHelper.setSpringConfigLocations(springConfigDirs);
	}
	
	protected ApplicationContext getApplicationContext() {
		return springConfigHelper.getApplicationContext();
	}

	protected ApplicationContext getApplicationContext(String springConfigDirs) {
		SpringConfigHelper sch = new SpringConfigHelper();
		sch.setSpringConfigLocations(springConfigDirs);
		return sch.getApplicationContext();
	}

	protected Method getSetterByName(String propName) {
		
		log().debug("Trying to find setter method for: " + propName);
		String methodName = "set" + propName.substring(0,1).toUpperCase() + propName.substring(1);

		for(Method m: this.getClass().getMethods()) {
			if (m.getName().equals(methodName)) {
				if (m.getParameterTypes().length == 1) {
					return m;
				}
			}
		}
			
		return null;
	}
	
	protected void handleArguments(String[] args) {
		for(int i=0; i<args.length; i++) {
			if (args[i].startsWith("-")) {
				String propName = args[i].substring(1);
				String value = null;
				if (args.length > (i+1) && !(args[i+1].startsWith("-"))) {
					value = args[++i];
				} else {
					value = Boolean.TRUE.toString();
				}
				try {
					Method m = getSetterByName(propName);
					if (m != null) {
						log().debug("Setting property: " + propName + "=" + value);
						m.invoke(this, value);
					}
				} catch (Exception e) {
					log().warn("Property " + propName + " not set.", e);
				}
			} else {
				log().warn("Ignoring command line argument: " + args[i]);
			}
		}
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
