package com.fusesource.forge.jmstest.executor;

import java.io.Serializable;

import com.fusesource.forge.jmstest.benchmark.command.TestRunConfig;

/**
 * @author  andreasgies
 */
public class BenchmarkNotificationPayload implements Serializable {

	private static final long serialVersionUID = -5494132649102005908L;
	
	/**
	 * @uml.property  name="config"
	 * @uml.associationEnd  
	 */
	private TestRunConfig config;
    /**
	 * @uml.property  name="endOfTest"
	 */
    private boolean endOfTest;
    /**
	 * @uml.property  name="clientConfigured"
	 */
    private boolean clientConfigured;

    public BenchmarkNotificationPayload(TestRunConfig config, boolean endOfTest) {
        this.config = config;
        this.endOfTest = endOfTest;
    }

    /**
	 * @return
	 * @uml.property  name="config"
	 */
    public TestRunConfig getConfig() {
        return config;
    }

    /**
	 * @return
	 * @uml.property  name="endOfTest"
	 */
    public boolean isEndOfTest() {
        return endOfTest;
    }

    /**
	 * @return
	 * @uml.property  name="clientConfigured"
	 */
    public boolean isClientConfigured() {
        return clientConfigured;
    }

    /**
	 * @param clientConfigured
	 * @uml.property  name="clientConfigured"
	 */
    public void setClientConfigured(boolean clientConfigured) {
        this.clientConfigured = clientConfigured;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BenchmarkNotificationPayload that = (BenchmarkNotificationPayload) o;

        if (endOfTest != that.endOfTest) {
            return false;
        }
        if (!config.equals(that.config)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = config.hashCode();
        result = 31 * result + (endOfTest ? 1 : 0);
        return result;
    }
}
