package com.fusesource.forge.jmstest.scenario;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BenchmarkIterationTest {

	@Test
	public void testOneStepOnlyIteration() {
		verifyIteration(createOneStepIteration(), new long[] {100});
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
	
	private BenchmarkIteration createOneStepIteration() {
		SimpleBenchmarkIteration simple = new SimpleBenchmarkIteration();
		simple.setInitialRate(100);
		simple.setMaxRate(100);
		
		return simple;
	}

	private BenchmarkIteration createSimpleIteration() {
		SimpleBenchmarkIteration simple = new SimpleBenchmarkIteration();
		simple.setInitialRate(5);
		simple.setMaxRate(10);
		simple.setIncrement(2);
		simple.setDuration(20);

		return simple;
	}
	
	private BenchmarkIteration createCompositeIteration() {
		List<BenchmarkIteration> list = new ArrayList<BenchmarkIteration>();
		list.add(createSimpleIteration());
		list.add(createOneStepIteration());
		CompositeBenchmarkIteration cbi = new CompositeBenchmarkIteration();
		cbi.setIterations(list);
		
		return cbi;
	}
	
	private BenchmarkIteration createCompositeRepeatingIteration() {
		
		BenchmarkIteration cbi = createCompositeIteration();
		((CompositeBenchmarkIteration)cbi).setRepititions(2);
		return cbi;
	}
	
	private void verifyIteration(BenchmarkIteration bi, long [] sequence) {
		
		System.out.println("====");
		bi.startIteration();
		for(int i=0; i<sequence.length; i++) {
			long rate = bi.nextEffectiveRate();
			System.out.println(i + " " + rate);
			Assert.assertTrue(rate == sequence[i]);
			Assert.assertTrue(bi.needsMoreRuns() == (i < sequence.length - 1));
		}
	}
}


