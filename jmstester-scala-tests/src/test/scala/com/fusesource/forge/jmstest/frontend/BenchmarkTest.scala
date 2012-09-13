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
package com.fusesource.forge.jmstest.frontend

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

/**
 *
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
@RunWith(classOf[JUnitRunner])
class BenchmarkTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers{

  test("Test benchmark handle arguments"){
    val benchmark = new Benchmark
    benchmark.start(Array[String](
      "-controller", "-recorder",
      "-clientNames", "christian,posta",
      "-hostname", "christianposta.com",
      "-destinationName", "test.topic",
      "-jmsPort", "12345"))
    assert(benchmark.isController)
    assert(benchmark.isRecorder)
    benchmark.getClientNames should equal("christian,posta")
    benchmark.getHostname should equal("christianposta.com")
    benchmark.getDestinationName should equal("test.topic")

  }

  override protected def beforeEach() {
    println("You're starting your test")
  }

  override protected def afterEach() {
    println("You're ending your test")
  }
}
