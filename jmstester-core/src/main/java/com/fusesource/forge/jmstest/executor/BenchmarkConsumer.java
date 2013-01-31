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
package com.fusesource.forge.jmstest.executor;

import javax.jms.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfigurationException;
import com.fusesource.forge.jmstest.peristence.BenchmarkSamplePersistenceAdapter;
import com.fusesource.forge.jmstest.probe.AveragingProbe;
import com.fusesource.forge.jmstest.probe.CountingProbe;
import com.fusesource.forge.jmstest.probe.ProbeRunner;
import com.fusesource.forge.jmstest.threading.Releaseable;

public class BenchmarkConsumer extends AbstractJMSClientComponent implements
    MessageListener, Releaseable {
  private transient Log log;

  private MessageConsumer messageConsumer;
  private String clientId;

  private ProbeRunner probeRunner;
  private CountingProbe msgCounterProbe;
  private AveragingProbe latencyProbe;
  private AveragingProbe msgSizeProbe;

  private BenchmarkSamplePersistenceAdapter adapter;

  public BenchmarkConsumer(AbstractBenchmarkJMSClient container, int clientId,
      BenchmarkSamplePersistenceAdapter adapter, ProbeRunner probeRunner) {
    super(container);
    setClientId(clientId);
    setProbeRunner(probeRunner);
    setBenchmarkSamplePersistenceAdapter(adapter);

    if (getProbeRunner() != null) {
      getProbeRunner().addProbe(getMsgCounterProbe());
      getProbeRunner().addProbe(getLatencyProbe());
      getProbeRunner().addProbe(getMsgSizeProbe());
    }
  }

  public void setClientId(int clientId) {
    this.clientId = getContainer().getClientId() + "-" + clientId;
  }

  public String getClientId() {
    return this.clientId;
  }

  public CountingProbe getMsgCounterProbe() {
    if (msgCounterProbe == null) {
      msgCounterProbe = new CountingProbe(getClientId() + "-Counter");
      msgCounterProbe.addObserver(getBenchmarkSamplePersistenceAdapter());
    }
    return msgCounterProbe;
  }

  public void setMsgCounterProbe(CountingProbe msgCounterProbe) {
    this.msgCounterProbe = msgCounterProbe;
  }

  public AveragingProbe getLatencyProbe() {
    if (latencyProbe == null) {
      latencyProbe = new AveragingProbe(getClientId() + "-Latency");
      latencyProbe.setResetOnRead(true);
      latencyProbe.addObserver(getBenchmarkSamplePersistenceAdapter());
    }
    return latencyProbe;
  }

  public void setLatencyProbe(AveragingProbe latencyProbe) {
    this.latencyProbe = latencyProbe;
  }

  public AveragingProbe getMsgSizeProbe() {
    if (msgSizeProbe == null) {
      msgSizeProbe = new AveragingProbe(getClientId() + "-MsgSize");
      msgSizeProbe.setResetOnRead(true);
      msgSizeProbe.addObserver(getBenchmarkSamplePersistenceAdapter());
    }
    return msgSizeProbe;
  }

  public void setMsgSizeProbe(AveragingProbe msgSizeProbe) {
    this.msgSizeProbe = msgSizeProbe;
  }

  public ProbeRunner getProbeRunner() {
    return probeRunner;
  }

  public void setProbeRunner(ProbeRunner probeRunner) {
    this.probeRunner = probeRunner;
  }

  public BenchmarkSamplePersistenceAdapter getBenchmarkSamplePersistenceAdapter() {
    return adapter;
  }

  public void setBenchmarkSamplePersistenceAdapter(
      BenchmarkSamplePersistenceAdapter adapter) {
    this.adapter = adapter;
  }

    @Override
    public Connection getConnection() throws Exception {
        Connection connection = super.getConnection();
        assert connection != null;

        // should have this usually, but definitely for durable subscriber
        if (connection.getClientID() == null || connection.getClientID().isEmpty()) {
            log().info("Setting client ID!! yip yip");
            connection.setClientID(getClientId());
        }
        return connection;
    }

    // TODO: simulate slow subscriber
    public void prepare() {
        try {

            Destination dest = getDestinationProvider().getDestination(getSession(),
                    getContainer().getPartConfig().getTestDestinationName());
            if (isDurable() && isTopic(dest)) {
                messageConsumer = getSession().createDurableSubscriber((Topic) dest, getClientId() + "_Durable_Subscriber");
                log().info("setting durable subscriber");
            }else {
                messageConsumer = getSession().createConsumer(dest);
            }
            assert messageConsumer != null;
            messageConsumer.setMessageListener(this);
        } catch (Exception e) {
            throw new BenchmarkConfigurationException(
                    "Unable to initialise JMS connection", e);
        }
    }

    private boolean isTopic(Destination dest) {
        return dest instanceof Topic;
    }

    private boolean isDurable() {
        return getContainer().getPartConfig().isDurableSubscription();
    }

    public void start() {
    try {
      if (getConnection() != null) {
        getConnection().start();
      }
    } catch (Exception e) {
      log().error("Connection could not be started.", e);
    }
  }

  public void onMessage(Message message) {
    // TODO: simulate slow subscriber with wait(n)
    try {
      long now = System.currentTimeMillis();
      long latency = now - message.getLongProperty("SendTime");
      if (getMsgCounterProbe() != null) {
        getMsgCounterProbe().increment();
      }
      if (getLatencyProbe() != null) {
        getLatencyProbe().addValue(new Double(latency));
      }
      if (getMsgSizeProbe() != null) {
        getMsgSizeProbe().addValue(new Double(getMessageSize(message)));
      }
    } catch (JMSException e) {
      log().warn("SendTime not available in message properties", e);
    }
  }

  private int getMessageSize(Message message) {
    int messageSize = 0;
    try {
      if (message instanceof BytesMessage) {
        messageSize = (int) ((BytesMessage) message).getBodyLength();
      } else if (message instanceof TextMessage) {
        messageSize = ((TextMessage) message).getText().length();
      }
    } catch (JMSException e) {
      log().warn("Failed to obtain message size by reading message body", e);
    }
    return messageSize;
  }

  public void release() {
    log().debug("Releasing Consumer for: " + getContainer().getClientId());
    super.release();
    log().debug("Released Consumer for: " + getContainer().getClientId());
  }

  protected Log log() {
    if (log == null) {
      log = LogFactory.getLog(getClass());
    }
    return log;
  }
}
