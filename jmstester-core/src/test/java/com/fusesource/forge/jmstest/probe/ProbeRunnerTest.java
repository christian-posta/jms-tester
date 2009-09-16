package com.fusesource.forge.jmstest.probe;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ProbeRunnerTest implements Observer {
	
	private final static int RUN_COUNT = 10;
	
	public void update(Observable o, Object arg) {
		((CountingProbe)((Object)o)).increment();
	}
	
	@Test
	public void runProbes() {
		CountingProbe p = new CountingProbe("CountingProbe");
		p.addObserver(this);
		ProbeRunner pr = new ProbeRunner();
		pr.setDuration(RUN_COUNT);
		pr.setInterval(1);
		pr.setName("Test");
		List<Probe> probes = new ArrayList<Probe>();
		probes.add(p);
		pr.setProbes(probes);
		
		try {
			pr.start();
			pr.waitUntilFinished();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Assert.assertTrue((RUN_COUNT-1 <= p.getValue().intValue()) && (p.getValue().intValue() <= RUN_COUNT+1));
	}
}
