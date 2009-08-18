package com.fusesource.forge.jmstest.executor;

import java.util.Observable;
import java.util.Observer;

import com.fusesource.forge.jmstest.benchmark.command.TestRunConfig;

/**
 * @author  andreasgies
 */
public abstract class ExecutableBenchmarkComponent implements Observer, Releaseable {

	private BenchmarkRunStatus benchmarkStatus;
    private TestRunConfig testRunConfig;

    protected ExecutableBenchmarkComponent() {
    }

    public void setTestRunConfig(TestRunConfig testRunConfig) {
    	this.testRunConfig = testRunConfig;
    }
    
    public TestRunConfig getTestRunConfig() {
    	return testRunConfig;
    }
    
    public void observeStatus(BenchmarkRunStatus status) {
        this.benchmarkStatus = status;
        status.addObserver(this);
    }

    public BenchmarkRunStatus getBenchmarkStatus() {
        return benchmarkStatus;
    }

    public void update(Observable o, Object arg) {
        if (arg.equals(BenchmarkRunStatus.State.FAILED) || arg.equals(BenchmarkRunStatus.State.COMPLETED)) {
            release();
        }
    }
}
