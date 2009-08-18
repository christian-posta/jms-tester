package com.fusesource.forge.jmstest.executor;

import java.util.Observable;
import java.util.Observer;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkPartConfig;

/**
 * @author  andreasgies
 */
public abstract class ExecutableBenchmarkComponent implements Observer, Releaseable {

	private BenchmarkRunStatus benchmarkStatus;
    private BenchmarkPartConfig testRunConfig;

    protected ExecutableBenchmarkComponent() {
    }

    public void setTestRunConfig(BenchmarkPartConfig testRunConfig) {
    	this.testRunConfig = testRunConfig;
    }
    
    public BenchmarkPartConfig getTestRunConfig() {
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
