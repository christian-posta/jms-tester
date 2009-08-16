package com.fusesource.forge.jmstest.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.config.BrokerServicesFactory;
import com.fusesource.forge.jmstest.config.TestRunConfig;
import com.fusesource.forge.jmstest.probe.ProbeRunner;
import com.fusesource.forge.jmstest.rrd.RRDController;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public class AbstractTestNGSpringJMSTest extends AbstractTestNGSpringContextTests {

	private transient Log log = null; 

	protected Object getBeanByClass(Class clazz) {
		Object result = null;
		String [] beanNames = applicationContext.getBeanNamesForType(clazz);
		if (beanNames != null && beanNames.length > 0) {
			log().debug("Using bean (" + beanNames[0] + " for type: " + clazz.getName());
			result = applicationContext.getBean(beanNames[0]);
		}
		if (beanNames.length > 1) {
			log().warn("Found " + beanNames.length + " beans for type " + clazz.getName());
		}
		return result;
	}
	
	public BrokerServicesFactory getBrokerServicesFactory() {
		return (BrokerServicesFactory)getBeanByClass(BrokerServicesFactory.class);
	}

	private void startRRDBackends() {
		String [] beanNames = applicationContext.getBeanNamesForType(RRDController.class);
		
		for(String name: beanNames) {
			RRDController controller = (RRDController)applicationContext.getBean(name);
			controller.setArchiveLength((int)((BenchmarkContext.getInstance().getProfile().getTotalDuration() + 5) / controller.getStep() + 1));
			try {
				controller.start();
			} catch (Exception e) {
				Assert.fail("Could not start RRD Backend", e);
			}
		}
	}
	
	private void startProbeRunner() {
		String [] beanNames = applicationContext.getBeanNamesForType(ProbeRunner.class);
		
		for(String name: beanNames) {
			ProbeRunner runner = (ProbeRunner)applicationContext.getBean(name);
			runner.setDuration(BenchmarkContext.getInstance().getProfile().getTotalDuration());
			runner.start();
		}
	}

	@BeforeClass
	public void setUp() {
		try {
			log().info("Initializing Test ...");
			BrokerServicesFactory bsf = getBrokerServicesFactory();
			if (bsf == null) {
				log().warn("No BrokerServicesFactory configured in Test ...");
			} else {
			    bsf.startAll();
			}
			
			BenchmarkIteration profile = (BenchmarkIteration)getBeanByClass(BenchmarkIteration.class);
			if (profile == null) {
				log().error("No profile set for Testrun.");
				Assert.fail();
			} else {
				BenchmarkContext.getInstance().setProfile(profile);
			}
			
			TestRunConfig testrunConfig = (TestRunConfig)getBeanByClass(TestRunConfig.class);
			if (profile == null) {
				log().error("No testrun config set for Testrun.");
				Assert.fail();
			} else {
				BenchmarkContext.getInstance().setTestrunConfig(testrunConfig);
			}
			
			startRRDBackends();
			startProbeRunner();
		} catch (Exception e) {
			Assert.fail("Unexpected exception setting up test", e);
		}
	}
	
	@AfterClass
	public void tearDown() {
		try {
			log().info("Test finished ...");
			BrokerServicesFactory bsf = getBrokerServicesFactory();
			if (bsf == null) {
				log().warn("No Brokers to shut down.");
			} else {
				getBrokerServicesFactory().stopAll();
			}
		} catch (Exception e) {
			Assert.fail("Unexpected exception cleaning up test", e);
		}
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
