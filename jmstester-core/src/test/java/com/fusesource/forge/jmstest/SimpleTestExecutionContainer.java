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
package com.fusesource.forge.jmstest;

import com.fusesource.forge.jmstest.benchmark.command.handler.BenchmarkCommandHandler;
import com.fusesource.forge.jmstest.executor.AbstractBenchmarkExecutionContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
public abstract class SimpleTestExecutionContainer extends AbstractBenchmarkExecutionContainer {
    private Log log = LogFactory.getLog(this.getClass());

    @Override
    protected void createHandlerChain() {
        super.createHandlerChain();
        getConnector().addHandler(createTestHandler());
    }

    protected abstract BenchmarkCommandHandler createTestHandler();

    public void start() {
        start(new String[]{});
    }

}
