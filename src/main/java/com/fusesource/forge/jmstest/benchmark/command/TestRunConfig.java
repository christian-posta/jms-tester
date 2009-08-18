/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.benchmark.command;

import java.io.Serializable;

import com.fusesource.forge.jmstest.config.AcknowledgeMode;
import com.fusesource.forge.jmstest.config.DeliveryMode;

public class TestRunConfig implements Serializable {

	private static final long serialVersionUID = -6606481736821036885L;
	
	private String runId;
    private AcknowledgeMode acknowledgeMode;
    private DeliveryMode deliveryMode;
    private boolean transacted;
    private int numConsumers = 1;
	private String adminFromProducer;
	private String adminFromConsumer;
	private String testDestinationName;

    public void setRunId(String runId) {
		this.runId = runId;
	}
    
    public String getRunId() {
		return runId;
	}

    public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
		this.acknowledgeMode = acknowledgeMode;
	}
    
    public AcknowledgeMode getAcknowledgeMode() {
		return acknowledgeMode;
	}

    public void setDeliveryMode(DeliveryMode deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
    
    public DeliveryMode getDeliveryMode() {
		return deliveryMode;
	}

    public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}
    
    public boolean isTransacted() {
		return transacted;
	}

    public int getNumConsumers() {
		return numConsumers;
	}

	public void setNumConsumers(int numConsumers) {
		this.numConsumers = numConsumers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((acknowledgeMode == null) ? 0 : acknowledgeMode.hashCode());
		result = prime
				* result
				+ ((adminFromConsumer == null) ? 0 : adminFromConsumer
						.hashCode());
		result = prime
				* result
				+ ((adminFromProducer == null) ? 0 : adminFromProducer
						.hashCode());
		result = prime * result
				+ ((deliveryMode == null) ? 0 : deliveryMode.hashCode());
		result = prime * result + numConsumers;
		result = prime * result + ((runId == null) ? 0 : runId.hashCode());
		result = prime
				* result
				+ ((testDestinationName == null) ? 0 : testDestinationName
						.hashCode());
		result = prime * result + (transacted ? 1231 : 1237);
		return result;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TestRunConfig{");
        builder.append(runId);
        builder.append(",");
        builder.append(acknowledgeMode.name());
        builder.append(",");
        builder.append(deliveryMode.name());
        builder.append(",");
        builder.append(transacted);
        builder.append(",");
        builder.append(testDestinationName);
        builder.append(",");
        builder.append(adminFromProducer);
        builder.append(",");
        builder.append(adminFromConsumer);
        builder.append("}");
        return builder.toString();
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestRunConfig other = (TestRunConfig) obj;
		if (acknowledgeMode == null) {
			if (other.acknowledgeMode != null)
				return false;
		} else if (!acknowledgeMode.equals(other.acknowledgeMode))
			return false;
		if (adminFromConsumer == null) {
			if (other.adminFromConsumer != null)
				return false;
		} else if (!adminFromConsumer.equals(other.adminFromConsumer))
			return false;
		if (adminFromProducer == null) {
			if (other.adminFromProducer != null)
				return false;
		} else if (!adminFromProducer.equals(other.adminFromProducer))
			return false;
		if (deliveryMode == null) {
			if (other.deliveryMode != null)
				return false;
		} else if (!deliveryMode.equals(other.deliveryMode))
			return false;
		if (numConsumers != other.numConsumers)
			return false;
		if (runId == null) {
			if (other.runId != null)
				return false;
		} else if (!runId.equals(other.runId))
			return false;
		if (testDestinationName == null) {
			if (other.testDestinationName != null)
				return false;
		} else if (!testDestinationName.equals(other.testDestinationName))
			return false;
		if (transacted != other.transacted)
			return false;
		return true;
	}

	public String getTestDestinationName() {
		return testDestinationName;
	}

	public void setTestDestinationName(String testDestinationName) {
		this.testDestinationName = testDestinationName;
	}


	public String getAdminFromProducer() {
		return adminFromProducer;
	}

	public void setAdminFromProducer(String adminFromProducer) {
		this.adminFromProducer = adminFromProducer;
	}

	public String getAdminFromConsumer() {
		return adminFromConsumer;
	}

	public void setAdminFromConsumer(String adminFromConsumer) {
		this.adminFromConsumer = adminFromConsumer;
	}
}
