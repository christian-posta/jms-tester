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
package com.fusesource.forge.jmstest.benchmark.command;

import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.executor.BenchmarkClient;
import com.fusesource.forge.jmstest.executor.BenchmarkController;
import com.fusesource.forge.jmstest.executor.BenchmarkJMSConsumerWrapper;
import com.fusesource.forge.jmstest.executor.BenchmarkProbeWrapper;
import com.fusesource.forge.jmstest.probe.jmx.JMXConnectionFactory;

@ContextConfiguration(locations = {
    "classpath:testScripts/distributed/benchmarks.xml",
    "classpath:testScripts/distributed/beans.xml" })
public class BenchmarkConfigTestManual extends AbstractTestNGSpringContextTests {

  private String[] clientNames = { "ERP-1", "ERP-2", "DWH-1", "DWH-2",
      "Monitor-1", "Monitor-2" };

  private BenchmarkConfig config = null;
  private BenchmarkController controller = null;
  private int jmsPort = 62626;
  private Log log = null;

  @BeforeTest
  public void startTest() throws Exception {
    controller = new BenchmarkController();
    controller.setHostname("0.0.0.0");
    controller.setJmsPort(0);
    controller.setAutoTerminate(false);
    controller.start(new String[] {});
    jmsPort = controller.getJmsPort();
  }

  @AfterTest
  public void stopTest() {
    controller.stop();
  }

  private BenchmarkConfig getConfig() {
    if (config == null) {
      config = (BenchmarkConfig) applicationContext.getBean("distributed");
      List<String> configLocations = new ArrayList<String>();
      configLocations.add("src/main/resources/testScripts/distributed");
      config.setConfigLocations(configLocations);

      Assert.assertEquals(config.getBenchmarkId(), "distributed");
    }
    return config;
  }

  private void checkPartNames() {
    String[] queuePartIDs = new String[] { "ERPToDWH", "DWHToERP" };

    String[] topicPartIDs = new String[] { "Events" };

    List<BenchmarkPartConfig> parts = getConfig().getBenchmarkParts();
    List<String> partNames = new ArrayList<String>();

    for (BenchmarkPartConfig part : parts) {
      partNames.add(part.getPartID());
    }

    for (String partId : queuePartIDs) {
      log().info("Checking partID: " + partId);
      Assert.assertTrue(partNames.contains(partId));
    }

    for (String partId : topicPartIDs) {
      log().info("Checking partID: " + partId);
      Assert.assertTrue(partNames.contains(partId));
    }
  }

  private List<String> matchingClients(int mode, BenchmarkPartConfig part) {
    List<String> result = new ArrayList<String>();

    for (String clientName : clientNames) {
      BenchmarkClient client = new BenchmarkClient();
      client.setName(clientName);
      client.setJmsPort(jmsPort);
      switch (mode) {
      case 0: {
        if (client.matchesClient(part.getConsumerClients())) {
          result.add(clientName);
        }
        break;
      }
      case 1: {
        if (client.matchesClient(part.getProducerClients())) {
          result.add(clientName);
        }
        break;
      }
      case 2: {
        BenchmarkProbeWrapper bpw = new BenchmarkProbeWrapper(client,
            getConfig());
        bpw.prepare();
        if (bpw.getProbeDescriptors().size() > 0) {
          result.add(clientName);
        }
        bpw.release();
        break;
      }
      default:
        // ignore
      }
      client.release();
    }
    return result;
  }

  private void checkBenchmarkParts() {
    for (BenchmarkPartConfig part : getConfig().getBenchmarkParts()) {
      log().info("Analyzing Benchmark Part: " + part.getPartID());
      Assert.assertTrue(part.getTestDestinationName()
          .endsWith(part.getPartID()));

      int consumerCount = matchingClients(0, part).size();
      int producerCount = matchingClients(1, part).size();

      if (part.getTestDestinationName().startsWith("topic:")) {
        Assert.assertEquals(consumerCount, 4);
        Assert.assertEquals(producerCount, 2);
      } else {
        Assert.assertEquals(consumerCount, 2);
        Assert.assertEquals(producerCount, 1);
      }

      checkClients(part);
    }
  }

  private void checkClients(BenchmarkPartConfig partConfig) {
    for (String clientName : clientNames) {
      BenchmarkClient client = new BenchmarkClient();
      client.setName(clientName);
      if (clientName.matches("(ERP|DWH)-(.)*")) {
        BenchmarkJMSConsumerWrapper bcw = new BenchmarkJMSConsumerWrapper(
            client, partConfig);
        String connFactoryName = bcw.getPreferredConnectionFactoryName();
        Assert.assertTrue(connFactoryName.startsWith("node"
            + clientName.substring(clientName.length())));
      }
    }
  }

  @Test
  public void testBenchmarkConfig() {
    String[] beanNames = getConfig().getApplicationContext()
        .getBeanNamesForType(JMXConnectionFactory.class);
    Assert.assertEquals(beanNames.length, 2);

    beanNames = getConfig().getApplicationContext().getBeanNamesForType(
        ConnectionFactory.class);
    Assert.assertEquals(beanNames.length, 2);

    checkPartNames();
    checkBenchmarkParts();

    Assert.assertEquals(matchingClients(2, null).size(), 2);
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
