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

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.executor.AbstractTestNGSpringJMSTest;

@ContextConfiguration(locations = {
    "classpath:com/fusesource/forge/jmstest/tests/simple/broker-services.xml",
    "classpath:com/fusesource/forge/jmstest/tests/simple/test-beans.xml",
    "classpath:com/fusesource/forge/jmstest/tests/simple/profiles.xml" })
public class SimpleTest extends AbstractTestNGSpringJMSTest {

  @Test
  public void benchmark() {
    super.benchmark();
  }
}
