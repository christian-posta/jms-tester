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
package com.fusesource.forge.jmstest.tests;

import java.rmi.server.UID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.config.DeliveryMode;
import com.fusesource.forge.jmstest.message.MessageFactory;

public class AsyncProducer extends AsyncClient implements Runnable,
    MessageListener {

  private static final transient Log LOG = LogFactory
      .getLog(AsyncProducer.class);

  private long msgToSend = -1;
  private DeliveryMode deliveryMode = DeliveryMode.NON_PERSISTENT;

  private MessageFactory messageFactory = null;

  private AtomicLong sent = null;
  private AtomicLong exceptions = null;
  private AtomicLong timeOuts = null;
  private AtomicBoolean done = new AtomicBoolean(false);
  private Thread myThread = null;
  private long ttl = 0L;
  private long sleep = 0L;
  private long replyTimeOut = 5000L;
  private long locallySent = 0;
  private boolean expectReply = false;
  private String replyDestination;
  private String msgGroup = null;

  private String corrId = null;
  private boolean receivedResponse = false;

  public long getReplyTimeOut() {
    return replyTimeOut;
  }

  public void setReplyTimeOut(long replyTimeOut) {
    this.replyTimeOut = replyTimeOut;
  }

  public String getMsgGroup() {
    return msgGroup;
  }

  public void setMsgGroup(String msgGroup) {
    this.msgGroup = msgGroup;
  }

  public void setTTL(final long ttl) {
    this.ttl = ttl;
  }

  private DeliveryMode getDeliveryMode() {
    return deliveryMode;
  }

  public void setDeliveryMode(final DeliveryMode newMode) {
    deliveryMode = newMode;
  }

  public void setSleep(final long sleep) {
    this.sleep = sleep;
  }

  public void setMessagesToSend(long msgToSend) {
    this.msgToSend = msgToSend;
  }

  public long getMessagesToSend() {
    return msgToSend;
  }

  public boolean isExpectReply() {
    return expectReply;
  }

  public void setExpectReply(boolean expectReply) {
    this.expectReply = expectReply;
  }

  public AtomicLong getSent() {
    return sent;
  }

  public void setSent(AtomicLong sent) {
    this.sent = sent;
  }

  public AtomicLong getExceptions() {
    return exceptions;
  }

  public void setExceptions(AtomicLong exceptions) {
    this.exceptions = exceptions;
  }

  public AtomicLong getTimeOuts() {
    return timeOuts;
  }

  public void setTimeOuts(AtomicLong timeOuts) {
    this.timeOuts = timeOuts;
  }

  public MessageFactory getMessageFactory() {
    return messageFactory;
  }

  public void setMessageFactory(MessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  public String getReplyDestination() {
    if (replyDestination == null) {
      replyDestination = "queue:Reply." + getName() + "."
          + new UID().toString().hashCode();
      LOG.debug("Producer using replyTo Destination : " + replyDestination);
    }
    return replyDestination;
  }

  public void setReplyDestination(String replyDestination) {
    this.replyDestination = replyDestination;
  }

  public void doStart() {
    if (myThread == null) {
      myThread = new Thread(this, getName());
      myThread.start();
    }
  }

  public Thread getThread() {
    return myThread;
  }

  public void onMessage(Message msg) {
    try {
      String recCorrId = msg.getJMSCorrelationID();
      synchronized (corrId) {
        if (recCorrId.equals(corrId)) {
          receivedResponse = true;
          String servedBy = msg.getStringProperty("ServedBy");
          LOG.debug("Served By : " + servedBy);
          String brokerStamp = msg.getStringProperty("BrokerStamp");
          LOG.debug("BrokerStamp : " + brokerStamp);
          corrId.notifyAll();
        }
      }
    } catch (JMSException jme) {
      LOG.error(jme);
    }
  }

  public void run() {

    try {
      Connection conn = getConnection();
      Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

      if (isExpectReply()) {
        Destination replyTo = getDestinationProvider().getDestination(session,
            getReplyDestination());
        MessageConsumer consumer = session.createConsumer(replyTo);
        consumer.setMessageListener(this);
      }

      Destination dest = getDestinationProvider().getDestination(session,
          getDestinationName());
      MessageProducer producer = session.createProducer(dest);
      producer.setDeliveryMode(getDeliveryMode().getCode());
      getConnection().start();

      LOG.info(">>Starting Message send loop");
      while (!done.get()) {
        try {
          locallySent++;
          Destination replyDest = null;
          Message msg = getMessageFactory().createMessage(session);
          if (getMsgGroup() != null) {
            LOG.debug("Setting message group to : " + getMsgGroup());
            msg.setStringProperty("JMSXGroupID", getMsgGroup());
            if (getMessagesToSend() > 0) {
              if (locallySent == getMessagesToSend()) {
                LOG.debug("Closing message group: " + getMsgGroup());
                msg.setIntProperty("JMSXGroupSeq", 0);
              }
            }
          }
          msg.setLongProperty("MsgNr", locallySent);
          if (isExpectReply()) {
            corrId = getReplyDestination() + "Seq-" + locallySent;
            msg.setStringProperty("JMSCorrelationID", corrId);
            replyDest = getDestinationProvider().getDestination(session,
                getReplyDestination());
            msg.setJMSReplyTo(replyDest);
            receivedResponse = false;
          }
          long sendTime = System.currentTimeMillis();
          producer.send(msg, deliveryMode.getCode(), 4, ttl);
          if (sent != null) {
            sent.incrementAndGet();
          }
          done.set((getMessagesToSend() > 0)
              && ((locallySent) == getMessagesToSend()));
          if (isExpectReply()) {
            try {
              LOG.debug("Waiting for response ...");
              synchronized (corrId) {
                try {
                  if (getReplyTimeOut() > 0) {
                    corrId.wait(getReplyTimeOut());
                  } else {
                    corrId.wait();
                  }
                } catch (InterruptedException ie) {
                }
                if (receivedResponse) {
                  long duration = System.currentTimeMillis() - sendTime;
                  LOG.debug("Got response from peer in " + duration + " ms");
                } else {
                  LOG.error("Response not received within time frame...");
                  if (timeOuts != null) {
                    timeOuts.incrementAndGet();
                  }
                }
              }
            } catch (Exception e) {
              if (exceptions != null) {
                exceptions.incrementAndGet();
              }
            }
          }
          if (sleep > 0L) {
            try {
              Thread.sleep(sleep);
            } catch (InterruptedException ie) {
            }
          }
        } catch (JMSException e) {
          if (exceptions != null) {
            exceptions.incrementAndGet();
          }
        }
      }
    } catch (Exception e) {
    } finally {
      try {
        closeConnection();
      } catch (Throwable e) {
      }
    }
    LOG.info(">>MessageSender done...(" + sent + ")");
  }

  public void doStop() {
    done.set(true);
  }
}
