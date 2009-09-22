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

import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AsyncConsumer extends AsyncClient implements MessageListener {

  private final static Log LOG = LogFactory.getLog(AsyncConsumer.class);

  private AtomicLong receiveCount = null;
  private Session session = null;

  protected Session getSession() throws Exception {
    if (session == null) {
      session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    }
    return session;
  }

  public void doStart() {
    try {
      Destination dest = getDestination(getSession(), getDestinationName());
      MessageConsumer consumer = session.createConsumer(dest);
      consumer.setMessageListener(this);
      getConnection().start();
    } catch (Exception jme) {
      try {
        getConnection().close();
      } catch (Exception e) {
      }
    }
  }

  public void doStop() {
    try {
      closeConnection();
    } catch (Exception e) {
    }
  }

  public void onMessage(Message msg) {
    if (receiveCount != null) {
      receiveCount.incrementAndGet();
    }
    MessageProducer producer = null;
    try {
      Destination replyDest = msg.getJMSReplyTo();
      if (replyDest != null) {
        Message response = getSession().createTextMessage("Response");
        response.setStringProperty("ServedBy", getName());
        response.setJMSCorrelationID(msg.getJMSCorrelationID());
        for (Enumeration<?> en = msg.getPropertyNames(); en.hasMoreElements();) {
          String key = (String) en.nextElement();
          Object value = msg.getObjectProperty(key);
          if (key.equals("BrokerStamp")) {
            value = value.toString() + " --";
          }
          response.setObjectProperty(key, value);
        }
        producer = getSession().createProducer(replyDest);
        producer.send(response);
      }
    } catch (Exception e) {
      LOG.error(e);
    } finally {
      if (producer != null) {
        try {
          producer.close();
        } catch (Exception e) {
        }
      }
    }
  }

  public void setReceiveCount(AtomicLong receiveCount) {
    this.receiveCount = receiveCount;
  }
}
