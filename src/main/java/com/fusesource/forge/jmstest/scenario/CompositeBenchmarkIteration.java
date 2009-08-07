package com.fusesource.forge.jmstest.scenario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompositeBenchmarkIteration extends AbstractBenchmarkIteration {
	
	private List<BenchmarkIteration> iterations;
	private int repititions = 1;
	
    private Iterator<BenchmarkIteration> currentIteration;
    private int currentRepitition;
    private BenchmarkIteration currentSequence;
	
	public void setIterations(List<BenchmarkIteration> iterations) {
		this.iterations = iterations;
	}
	
	public List<BenchmarkIteration> getIterations() {
		if (iterations == null) {
			iterations = new ArrayList<BenchmarkIteration>();
		}
		return iterations;
	}
	
	public void setRepititions(int repititions) {
		this.repititions = Math.max(1, repititions);
	}

	public int getRepititions() {
		return repititions;
	}
	
	public void startIteration() {
		currentSequence = null;
		currentIteration = null;
		currentIteration = getIterations().iterator();
		currentRepitition = 1;
	}
	
	public boolean needsMoreRuns() {
		
		if (getIterations().size() == 0) {
			return false;
		}
		if (currentSequence != null) {
			if (currentSequence.needsMoreRuns()) {
				return true;
			}
		}
		if (currentIteration.hasNext()) {
			return true;
		}
		if (currentRepitition < repititions) {
			return true;
		}
		return false;
	}
	
	public long nextEffectiveRate() {

		adjustCurrentSequence();
		
		if (currentSequence != null) {
			return currentSequence.nextEffectiveRate();
		} else {
			return 1;
		}
	}
	
	public long getDuration() {
		if (currentSequence != null) {
			return currentSequence.getDuration();
		} else {
			return 1;
		}
	}
	
	private void adjustCurrentSequence() {
		if (currentSequence == null) {
			if (currentIteration.hasNext()) {
				currentSequence = currentIteration.next();
			}
		} else {
		  if (!currentSequence.needsMoreRuns()) {
			  if (currentIteration.hasNext()) {
				  currentSequence = currentIteration.next();
			  } else {
				  if (currentRepitition < repititions) {
					currentRepitition++;
				    currentIteration = getIterations().iterator();
				    for(Iterator<BenchmarkIteration> it=getIterations().iterator(); it.hasNext();) {
				    	it.next().startIteration();
				    }
				    currentSequence = currentIteration.next();
				  } else {
					  currentSequence = null;
				  }
			  }
		  }
		}
	}
}
