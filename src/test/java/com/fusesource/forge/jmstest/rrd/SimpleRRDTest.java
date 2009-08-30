package com.fusesource.forge.jmstest.rrd;

import java.util.ArrayList;
import java.util.List;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.probe.AbstractProbe;
import com.fusesource.forge.jmstest.probe.Probe;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;
import com.fusesource.forge.jmstest.probe.RandomProbe;

public class SimpleRRDTest {

	private final static int NUM_VALUES = 30;
	
	@Test
	public void rrdTest() {
		Probe randomProbe = new RandomProbe("RandomProbe", 1000.0);

		ProbeDescriptor pd = randomProbe.getDescriptor();
		List<ProbeDescriptor> descriptors = new ArrayList<ProbeDescriptor>();
		descriptors.add(pd);
		
		Rrd4jSamplePersistenceAdapter controller = new Rrd4jSamplePersistenceAdapter(descriptors);
		((AbstractProbe)randomProbe).addObserver(controller);

		controller.setStartTime(System.currentTimeMillis() / 1000 - 1);
		controller.setStep(1);
		controller.setArchiveLength((int) (NUM_VALUES / controller.getStep()));
		controller.setCacheSize(10);
		
		try {
			controller.start();
			
			long step = controller.getStep();
			
			for(int i=0; i<NUM_VALUES; i++) {
				randomProbe.probe();
				try {
					Thread.sleep(controller.getStep() * 1000);
				} catch (InterruptedException ie) {}
			}
			
			controller.release();

			RrdDb db = controller.getDatabase();
			Assert.assertEquals(db.getDsCount(), 1);
			FetchRequest fr = db.createFetchRequest(ConsolFun.AVERAGE, controller.getStartTime(), controller.getStartTime() + (NUM_VALUES-1) * step);
			FetchData data = fr.fetchData();
			System.out.println(data.dump());
		} catch (Exception e) {
			Assert.fail("Error accessing RRD database", e);
		}
	}
}
