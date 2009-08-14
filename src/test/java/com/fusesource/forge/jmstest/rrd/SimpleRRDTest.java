package com.fusesource.forge.jmstest.rrd;

import java.util.Random;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;

@ContextConfiguration(locations={
	"classpath:com/fusesource/forge/jmstest/rrd/RRDConfig.xml"})

public class SimpleRRDTest extends AbstractTestNGSpringContextTests {

	private final static int NUM_VALUES = 10;
	
	@Test
	public void rrdTest() {
		Random rnd = new Random(System.currentTimeMillis());
		
		long startTime = System.currentTimeMillis() / 1000;

		RRDController controller = (RRDController)applicationContext.getBean("RRDDatabase");
		RRDRecorder recorder = (RRDRecorder)applicationContext.getBean("RRDRecorder1");

		try {
			controller.start();
			
			long step = controller.getStep();
			
			for(int i=0; i<NUM_VALUES; i++) {
				recorder.record(startTime + i*step, rnd.nextDouble()*100);
			}
			
			controller.release();

			RrdDb db = controller.getDatabase();
			Assert.assertEquals(db.getDsCount(), 1);
			Assert.assertEquals(recorder.getName(), db.getDsNames()[0]);
			FetchRequest fr = db.createFetchRequest(ConsolFun.AVERAGE, startTime, startTime + (NUM_VALUES-1) * step);
			FetchData data = fr.fetchData();
			System.out.println(data.dump());
		} catch (Exception e) {
			Assert.fail("Error accessing RRD database", e);
		}
	}
}
