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
package com.fusesource.forge.jmstest.tests.simple;

import java.util.concurrent.CountDownLatch;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.tests.AbstractJMSTest;

public class SimpleConsumer extends AbstractJMSTest {
  private transient Log log = null;

  protected String[] getConfigLocations() {
    return new String[] {
    // "com/fusesource/forge/jmstest/tests/simple/broker-services.xml",
    "com/fusesource/forge/jmstest/tests/simple/test-beans.xml" };
  }

  protected void run() {
    Connection con = null;
    Session session = null;
    final CountDownLatch latch = new CountDownLatch(Integer.MAX_VALUE);
    try {
      con = getConnection();
      session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination dest = getDestinationProvider().getDestination(session,
          "queue:TEST");
      MessageConsumer consumer = session.createConsumer(dest);
      consumer.setMessageListener(new MessageListener() {
        public void onMessage(Message msg) {
          String grp = null;
          long nr = 0L;
          try {
            grp = msg.getStringProperty("JMSXGroupID");
            nr = msg.getLongProperty("MsgNr");
          } catch (JMSException jme) {
          }
          log().info("Received Message Group=(" + grp + ") MsgNr=" + nr);
          latch.countDown();
        }
      });
      con.start();
      latch.await();
      con.close();
    } catch (Exception e) {
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (Exception e) {
        }
      }
    }
  }

  public static void main(String[] args) {
    SimpleConsumer sc = new SimpleConsumer();
    sc.run();
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
