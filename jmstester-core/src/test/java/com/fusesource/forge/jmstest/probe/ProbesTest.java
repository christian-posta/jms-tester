package com.fusesource.forge.jmstest.probe;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ProbesTest {
	
	@Test
	public void testCountingProbe() {
		CountingProbe probe = new CountingProbe();
		Assert.assertEquals(probe.getValue(), new Long(0l));
		
		probe.increment();
		Assert.assertEquals(probe.getValue(), new Long(1l));
		
		probe.increment(10l);
		Assert.assertEquals(probe.getValue(), new Long(11l));
	}
	
	@Test
	public void testAveragingProbe() {
		
		int count = 100;
		
		AveragingProbe probe = new AveragingProbe();
		Assert.assertEquals(probe.getValue(), 0.0);

		for(int i=0; i<count; i++) {
			probe.addValue(1);
		}
		Assert.assertEquals(probe.getValue(), 1.0);
		
		probe.addValue(count);
		Assert.assertEquals(probe.getValue(), 200.0 / (count+1));
		
		probe.reset();
		Assert.assertEquals(probe.getValue(), 0.0);
	}

}
