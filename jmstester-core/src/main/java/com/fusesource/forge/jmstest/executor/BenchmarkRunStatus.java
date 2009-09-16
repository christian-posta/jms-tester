package com.fusesource.forge.jmstest.executor;

public class BenchmarkRunStatus {

    private State currentState = State.CREATED;

    public void setState(State state) {
    	this.currentState = state;
    }

    public State getCurrentState() {
        return currentState;
    }

    public static enum State {
    	CREATED,
        SUBMITTED,
        PREPARED,
        STARTED,
        FINISHED,
        FAILED
    }
}
