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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.command.SubmitBenchmarkCommand;
import com.fusesource.forge.jmstest.config.BrokerServicesFactory;
import com.fusesource.forge.jmstest.frontend.Benchmark;
import com.fusesource.forge.jmstest.frontend.CommandLineClient;

public class AbstractTestNGSpringJMSTest extends
    AbstractTestNGSpringContextTests {

  private Benchmark benchmark = null;
  private transient Log log = null;

  protected Object getBeanByClass(Class<?> clazz) {
    Object result = null;
    String[] beanNames = applicationContext.getBeanNamesForType(clazz);
    if (beanNames != null && beanNames.length > 0) {
      log().debug(
          "Using bean (" + beanNames[0] + " for type: " + clazz.getName());
      result = applicationContext.getBean(beanNames[0]);
    }
    if (beanNames.length > 1) {
      log().warn(
          "Found " + beanNames.length + " beans for type " + clazz.getName());
    }
    return result;
  }

  public BrokerServicesFactory getBrokerServicesFactory() {
    return (BrokerServicesFactory) getBeanByClass(BrokerServicesFactory.class);
  }

  @BeforeClass
  public void setUp() {
    try {
      log().info("Initializing Test ...");
      BrokerServicesFactory bsf = getBrokerServicesFactory();
      if (bsf == null) {
        log().warn("No BrokerServicesFactory configured in Test ...");
      } else {
        bsf.startAll();
      }

      benchmark = new Benchmark();
      benchmark.setController(Boolean.TRUE.toString());
      benchmark.setClientNames("TestClient");
      benchmark.setRecorder(Boolean.TRUE.toString());

    } catch (Exception e) {
      Assert.fail("Unexpected exception setting up test", e);
    }
  }

  public void benchmark() {
    benchmark.start(new String[] {});
    CommandLineClient clc = new CommandLineClient();
    String[] beanNames = applicationContext
        .getBeanNamesForType(BenchmarkConfig.class);
    for (String name : beanNames) {
      BenchmarkConfig cfg = (BenchmarkConfig) applicationContext.getBean(name);
      clc.sendCommand(new SubmitBenchmarkCommand(cfg));
    }
  }

  @AfterClass
  public void tearDown() {
    try {
      log().info("Test finished ...");
      BrokerServicesFactory bsf = getBrokerServicesFactory();
      if (bsf == null) {
        log().warn("No Brokers to shut down.");
      } else {
        getBrokerServicesFactory().stopAll();
      }
      ReleaseManager.getInstance().run();
    } catch (Exception e) {
      Assert.fail("Unexpected exception cleaning up test", e);
    }
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
