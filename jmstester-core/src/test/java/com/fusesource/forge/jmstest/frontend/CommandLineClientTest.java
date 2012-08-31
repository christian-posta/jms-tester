/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fusesource.forge.jmstest.frontend;

import com.fusesource.forge.jmstest.SimpleTestExecutionContainer;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkClientInfoCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.handler.BenchmarkCommandHandler;
import com.fusesource.forge.jmstest.benchmark.command.handler.DefaultCommandHandler;
import com.fusesource.forge.jmstest.executor.AbstractBenchmarkExecutionContainer;
import com.fusesource.forge.jmstest.executor.BenchmarkController;
import com.fusesource.forge.jmstest.util.JmsTesterUtils;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

/**
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
public class CommandLineClientTest {
    private Log log = LogFactory.getLog(this.getClass());

    private CommandLineClient commandLineClient;
    private BrokerService broker = null;


    @Before
    public void setUp() {
        try {
            getBroker().start();
            broker.waitUntilStarted();
        } catch (Exception e) {
            log.error("Error starting embedded broker", e);
        }
        commandLineClient = new CommandLineClient();
    }

    @After
    public void tearDown() {
        if (broker != null) {
            try {
                broker.stop();
                broker.waitUntilStopped();
            } catch (Exception e) {
                log.error("Could not properly stop the broker");
            }
        }
    }



    @Test
    public void testRunWithAbsoluteConfigLocation() throws InterruptedException {
        File benchmarkDir = JmsTesterUtils.getResourceAsFile("/simple");
        assertTrue(benchmarkDir.isDirectory());

        final CountDownLatch testPassedLatch = new CountDownLatch(1);
        SimpleTestExecutionContainer container = new SimpleTestExecutionContainer() {
            @Override
            protected BenchmarkCommandHandler createTestHandler() {
                return new DefaultCommandHandler() {
                    public boolean handleCommand(BenchmarkCommand command) {
                        if (command.getCommandType() == CommandTypes.SUBMIT_BENCHMARK) {
                            testPassedLatch.countDown();
                            return true;
                        }
                        return false;
                    }
                };
            }
        };
        container.start();

        // exercise unit
        commandLineClient.run(new String[]{"-command", "submit:" + benchmarkDir.getAbsolutePath()});

        assertTrue("CommandLineClient did not send a SUBMIT_BENCHMARK command", testPassedLatch.await(1, TimeUnit.SECONDS));

        container.stop();
    }

    @Test
    public void testRunWithRelativeConfigLocation() throws InterruptedException {
        File benchmarkDir = JmsTesterUtils.getResourceAsFile("/simple");
        assertTrue(benchmarkDir.isDirectory());

        final CountDownLatch testPassedLatch = new CountDownLatch(1);
        SimpleTestExecutionContainer container = new SimpleTestExecutionContainer() {
            @Override
            protected BenchmarkCommandHandler createTestHandler() {
                return new DefaultCommandHandler() {
                    public boolean handleCommand(BenchmarkCommand command) {
                        if (command.getCommandType() == CommandTypes.SUBMIT_BENCHMARK) {
                            testPassedLatch.countDown();
                            return true;
                        }
                        return false;
                    }
                };
            }
        };
        container.start();

        // exercise unit   note the relative path
        commandLineClient.run(new String[]{"-command", "submit:src/test/resources/simple"});

        assertTrue("CommandLineClient did not send a SUBMIT_BENCHMARK command", testPassedLatch.await(1, TimeUnit.SECONDS));
        container.stop();

    }

    private BrokerService getBroker() throws Exception {
        if (broker == null) {
            broker = new BrokerService();
            broker.addConnector("tcp://0.0.0.0:62626");
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.start();
            broker.waitUntilStarted();
        }
        return broker;
    }

}
