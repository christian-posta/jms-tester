package com.fusesource.forge.jmstest.executor;

import java.util.Observable;

public class BenchmarkRunStatus extends Observable {

    private State currentState = State.OK;

    public BenchmarkRunStatus() {
    }

    public void setState(State state) {
        if (!this.currentState.equals(state)) {
            this.currentState = state;
            setChanged();
            notifyObservers(this.currentState);
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    public void reinitialise() {
        this.currentState = State.OK;
    }

    public static enum State {
        OK,
        FAILED,
        COMPLETED
    }
}
