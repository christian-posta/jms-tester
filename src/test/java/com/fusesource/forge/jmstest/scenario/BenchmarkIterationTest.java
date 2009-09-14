package com.fusesource.forge.jmstest.scenario;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BenchmarkIterationTest {

	private final static int DURATION = 10;
	
	private Log log = null;
	
	@Test
	public void testOneStepOnlyIteration() {
		verifyIteration(createOneStepIteration(100), new long[] {100});
	}

	@Test
	public void testSimpleIteration() {
		verifyIteration(createSimpleIteration(), new long[] {5, 7, 9, 10});
	}

	@Test
	public void testCompositeIteration() {
		verifyIteration(createCompositeIteration(), new long[] {5, 7, 9, 10, 100});
	}
	
	@Test
	public void testCompositeRepeatingIteration() {
		verifyIteration(createCompositeRepeatingIteration(), new long[] {5, 7, 9, 10, 100, 5, 7, 9, 10, 100});
	}
	
	@Test
	public void testPeakingIteration() {
		verifyIteration(createPeakingIteration(), new long[] {100, 1000, 100});
	}
	
	private BenchmarkIteration createOneStepIteration(int rate) {
		SimpleBenchmarkIteration simple = new SimpleBenchmarkIteration();
		simple.setInitialRate(rate);
		simple.setDuration(DURATION);
		
		return simple;
	}

	private BenchmarkIteration createSimpleIteration() {
		SimpleBenchmarkIteration simple = new SimpleBenchmarkIteration();
		simple.setInitialRate(5);
		simple.setMaxRate(10);
		simple.setIncrement(2);
		simple.setDuration(DURATION);

		return simple;
	}
	
	private BenchmarkIteration createCompositeIteration() {
		List<BenchmarkIteration> list = new ArrayList<BenchmarkIteration>();
		list.add(createSimpleIteration());
		list.add(createOneStepIteration(100));
		CompositeBenchmarkIteration cbi = new CompositeBenchmarkIteration();
		cbi.setIterations(list);
		
		return cbi;
	}
	
	private BenchmarkIteration createCompositeRepeatingIteration() {
		
		BenchmarkIteration cbi = createCompositeIteration();
		((CompositeBenchmarkIteration)cbi).setRepititions(2);
		return cbi;
	}
	
	private BenchmarkIteration createPeakingIteration() {
		CompositeBenchmarkIteration cbi = new CompositeBenchmarkIteration();
		
		List<BenchmarkIteration> iterations = new ArrayList<BenchmarkIteration>();
		
		iterations.add(createOneStepIteration(100));
		iterations.add(createOneStepIteration(1000));
		iterations.add(createOneStepIteration(100));

		cbi.setIterations(iterations);
		return cbi;
	}
	
	private void verifyIteration(BenchmarkIteration bi, long [] sequence) {
		
		bi.startIteration();
		for(int i=0; i<sequence.length; i++) {
			long rate = bi.nextEffectiveRate();
			log().info(i + " " + rate);
			Assert.assertTrue(rate == sequence[i]);
			Assert.assertTrue(bi.needsMoreRuns() == (i < sequence.length - 1));
		}
		
		Assert.assertEquals(bi.getTotalDuration(), sequence.length * DURATION);
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}


