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

import org.springframework.beans.factory.ObjectFactory;

import com.fusesource.forge.jmstest.tests.AbstractJMSTest;
import com.fusesource.forge.jmstest.tests.AsyncProducer;

public class SimpleProducer extends AbstractJMSTest {

  private final static int NUM_PRODUCERS = 5;

  protected String[] getConfigLocations() {
    return new String[] {
    // "com/fusesource/forge/jmstest/tests/simple/broker-services.xml",
    "com/fusesource/forge/jmstest/tests/simple/test-beans.xml" };
  }

  protected void run() {
    ObjectFactory factory = (ObjectFactory) getApplicationContext().getBean(
        "AsyncProducerFactory");

    for (int i = 0; i < NUM_PRODUCERS; i++) {
      AsyncProducer producer = (AsyncProducer) factory.getObject();
      producer.setMsgGroup("MsgGroup-" + i);
      producer.start();
    }
  }

  public static void main(String[] args) {
    SimpleProducer bs = new SimpleProducer();
    bs.run();
  }

}
