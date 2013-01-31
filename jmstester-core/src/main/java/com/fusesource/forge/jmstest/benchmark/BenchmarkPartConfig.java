/*
 * Copyright (C) 2009, Progress Software Corporation and/or its
 * subsidiaries or affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fusesource.forge.jmstest.benchmark;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fusesource.forge.jmstest.config.AcknowledgeMode;
import com.fusesource.forge.jmstest.config.DeliveryMode;

public class BenchmarkPartConfig implements Serializable {

  private static final long serialVersionUID = -6606481736821036885L;

  private BenchmarkConfig parent;
  private String partID = null;
  private AcknowledgeMode acknowledgeMode;
  private DeliveryMode deliveryMode;
  private boolean transacted;
  private boolean durableSubscription;
  private int numConsumers = 1;
  private String testDestinationName;
  private int transactionBatchSize = 1;
  private String profileName;
  private String consumerClients = "ALL";
  private String producerClients = "ALL";
  private Map<String, String> connectionFactoryNames = null;
  private String jmsDestinationProviderName = null;
  private String messageFactoryName = null;
  private int maxConsumerRatePerThread = 1000;

  public BenchmarkConfig getParent() {
    return parent;
  }

    public boolean isDurableSubscription() {
        return durableSubscription;
    }

    public void setDurableSubscription(boolean durableSubscription) {
        this.durableSubscription = durableSubscription;
    }

    public void setParent(BenchmarkConfig parent) {
    this.parent = parent;
  }

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

  public Map<String, String> getConnectionFactoryNames() {
    if (connectionFactoryNames == null) {
      connectionFactoryNames = new HashMap<String, String>();
    }
    return connectionFactoryNames;
  }

  public void setConnectionFactoryNames(Map<String, String> connectionFactoryNames) {
    this.connectionFactoryNames = connectionFactoryNames;
  }

  public String getJmsDestinationProviderName() {
    return jmsDestinationProviderName;
  }

  public void setJmsDestinationProviderName(String jmsDestinationProviderName) {
    this.jmsDestinationProviderName = jmsDestinationProviderName;
  }

//  todo:ceposta this should be changed to "max Producer Rate Per Thread" since that's how it's used
  public int getMaxConsumerRatePerThread() {
    return maxConsumerRatePerThread;
  }

  public void setMaxConsumerRatePerThread(int maxConsumerRatePerThread) {
    this.maxConsumerRatePerThread = maxConsumerRatePerThread;
  }



  public String getMessageFactoryName() {
    return messageFactoryName;
  }

  public void setMessageFactoryName(String messageFactoryName) {
    this.messageFactoryName = messageFactoryName;
  }

  @Override
  public String toString() {
    return "BenchmarkPartConfig [acknowledgeMode=" + acknowledgeMode
        + ", consumerClients=" + consumerClients + ", deliveryMode="
        + deliveryMode + ", jmsDestinationProviderName="
        + jmsDestinationProviderName + ", messageFactoryName="
        + messageFactoryName + ", numConsumers=" + numConsumers + ", parent="
        + parent + ", partID=" + partID + ", producerClients="
        + producerClients + ", profileName=" + profileName
        + ", testDestinationName=" + testDestinationName + ", transacted="
        + transacted + ", transactionBatchSize=" + transactionBatchSize + "]";
  }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BenchmarkPartConfig that = (BenchmarkPartConfig) o;

        if (durableSubscription != that.durableSubscription) return false;
        if (maxConsumerRatePerThread != that.maxConsumerRatePerThread) return false;
        if (numConsumers != that.numConsumers) return false;
        if (transacted != that.transacted) return false;
        if (transactionBatchSize != that.transactionBatchSize) return false;
        if (acknowledgeMode != that.acknowledgeMode) return false;
        if (connectionFactoryNames != null ? !connectionFactoryNames.equals(that.connectionFactoryNames) : that.connectionFactoryNames != null)
            return false;
        if (consumerClients != null ? !consumerClients.equals(that.consumerClients) : that.consumerClients != null)
            return false;
        if (deliveryMode != that.deliveryMode) return false;
        if (jmsDestinationProviderName != null ? !jmsDestinationProviderName.equals(that.jmsDestinationProviderName) : that.jmsDestinationProviderName != null)
            return false;
        if (messageFactoryName != null ? !messageFactoryName.equals(that.messageFactoryName) : that.messageFactoryName != null)
            return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (partID != null ? !partID.equals(that.partID) : that.partID != null) return false;
        if (producerClients != null ? !producerClients.equals(that.producerClients) : that.producerClients != null)
            return false;
        if (profileName != null ? !profileName.equals(that.profileName) : that.profileName != null) return false;
        if (testDestinationName != null ? !testDestinationName.equals(that.testDestinationName) : that.testDestinationName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (partID != null ? partID.hashCode() : 0);
        result = 31 * result + (acknowledgeMode != null ? acknowledgeMode.hashCode() : 0);
        result = 31 * result + (deliveryMode != null ? deliveryMode.hashCode() : 0);
        result = 31 * result + (transacted ? 1 : 0);
        result = 31 * result + (durableSubscription ? 1 : 0);
        result = 31 * result + numConsumers;
        result = 31 * result + (testDestinationName != null ? testDestinationName.hashCode() : 0);
        result = 31 * result + transactionBatchSize;
        result = 31 * result + (profileName != null ? profileName.hashCode() : 0);
        result = 31 * result + (consumerClients != null ? consumerClients.hashCode() : 0);
        result = 31 * result + (producerClients != null ? producerClients.hashCode() : 0);
        result = 31 * result + (connectionFactoryNames != null ? connectionFactoryNames.hashCode() : 0);
        result = 31 * result + (jmsDestinationProviderName != null ? jmsDestinationProviderName.hashCode() : 0);
        result = 31 * result + (messageFactoryName != null ? messageFactoryName.hashCode() : 0);
        result = 31 * result + maxConsumerRatePerThread;
        return result;
    }
}
