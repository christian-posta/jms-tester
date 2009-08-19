/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.benchmark.command;

import java.io.Serializable;
import java.util.UUID;

import com.fusesource.forge.jmstest.config.AcknowledgeMode;
import com.fusesource.forge.jmstest.config.DeliveryMode;

public class BenchmarkPartConfig implements Serializable {

	private static final long serialVersionUID = -6606481736821036885L;
	
	private String partID = null;
    private AcknowledgeMode acknowledgeMode;
    private DeliveryMode deliveryMode;
    private boolean transacted;
    private int numConsumers = 1;
	private String testDestinationName;
	private int transactionBatchSize = 1;
	private String profileName;
	private String consumerClients = "ALL";
	private String producerClients = "ALL";

	public String getPartID() {
		if (partID == null) {
			partID = UUID.randomUUID().toString();
		}
		return partID;
	}

	public void setPartID(String partID) {
		this.partID = partID;
	}

	public boolean isAcceptAllProducers() {
		return getProducerClients().equalsIgnoreCase("all");
	}

	public String getProducerClients() {
		return producerClients;
	}

	public void setProducerClients(String producerClients) {
		this.producerClients = producerClients;
	}

	public boolean isAcceptAllConsumers() {
		return getConsumerClients().equalsIgnoreCase("all");
	}
	
	public String getConsumerClients() {
		return consumerClients;
	}

	public void setConsumerClients(String consumerClients) {
		this.consumerClients = consumerClients;
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

	public String getTestDestinationName() {
		return testDestinationName;
	}

	public void setTestDestinationName(String testDestinationName) {
		this.testDestinationName = testDestinationName;
	}

	public int getTransactionBatchSize() {
		return transactionBatchSize;
	}

	public void setTransactionBatchSize(int transactionBatchSize) {
		this.transactionBatchSize = transactionBatchSize;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((acknowledgeMode == null) ? 0 : acknowledgeMode.hashCode());
		result = prime * result
				+ ((deliveryMode == null) ? 0 : deliveryMode.hashCode());
		result = prime * result + numConsumers;
		result = prime * result
				+ ((profileName == null) ? 0 : profileName.hashCode());
		result = prime
				* result
				+ ((testDestinationName == null) ? 0 : testDestinationName
						.hashCode());
		result = prime * result + (transacted ? 1231 : 1237);
		result = prime * result + transactionBatchSize;
		return result;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName() + "{");
        builder.append(acknowledgeMode.name());
        builder.append(",");
        builder.append(deliveryMode.name());
        builder.append(",");
        builder.append(transacted);
        builder.append(",");
        builder.append(testDestinationName);
        builder.append(",");
        builder.append(profileName);
        builder.append(",");
        builder.append(transactionBatchSize);
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
		BenchmarkPartConfig other = (BenchmarkPartConfig) obj;
		if (acknowledgeMode == null) {
			if (other.acknowledgeMode != null)
				return false;
		} else if (!acknowledgeMode.equals(other.acknowledgeMode))
			return false;
		if (deliveryMode == null) {
			if (other.deliveryMode != null)
				return false;
		} else if (!deliveryMode.equals(other.deliveryMode))
			return false;
		if (numConsumers != other.numConsumers)
			return false;
		if (profileName == null) {
			if (other.profileName != null)
				return false;
		} else if (!profileName.equals(other.profileName))
			return false;
		if (testDestinationName == null) {
			if (other.testDestinationName != null)
				return false;
		} else if (!testDestinationName.equals(other.testDestinationName))
			return false;
		if (transacted != other.transacted)
			return false;
		if (transactionBatchSize != other.transactionBatchSize)
			return false;
		return true;
	}
}
