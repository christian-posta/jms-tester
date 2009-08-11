package com.fusesource.forge.jmstest.rrd;

import java.util.Random;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations={
	"classpath:com/fusesource/forge/jmstest/rrd/RRDConfig.xml"})

public class SimpleRRDTest extends AbstractTestNGSpringContextTests {

	@Test
	public void rrdTest() {
		Random rnd = new Random(System.currentTimeMillis());
		
		long startTime = System.currentTimeMillis() / 1000;

		RRDController controller = (RRDController)applicationContext.getBean("RRDDatabase");
		long step = controller.getStep();
		
		RRDRecorder recorder = (RRDRecorder)applicationContext.getBean("RRDRecorder1");
		
		for(int i=0; i<100; i++) {
			recorder.record(startTime + i*step, rnd.nextDouble()*100);
		}
			
	}

}
