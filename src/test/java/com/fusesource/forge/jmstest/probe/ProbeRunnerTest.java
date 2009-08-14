package com.fusesource.forge.jmstest.probe;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.rrd.RRDController;

@ContextConfiguration(locations={
	"classpath:com/fusesource/forge/jmstest/probe/ProbeConfig.xml"})

public class ProbeRunnerTest extends AbstractTestNGSpringContextTests{
	
	@BeforeMethod
	public void init() {
	}
	
	@Test
	public void runProbes() {
		try {
			RRDController controller = (RRDController)applicationContext.getBean("RRDDatabase");
			controller.start();
			ProbeRunner runner = (ProbeRunner)applicationContext.getBean("ProbeRunner");
			runner.start();
			runner.waitUntilFinished();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
