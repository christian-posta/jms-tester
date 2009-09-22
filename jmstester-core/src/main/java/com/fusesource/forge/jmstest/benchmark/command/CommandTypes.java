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

public interface CommandTypes {

  byte NULL = 0;

  byte SUBMIT_BENCHMARK = 10;
  byte PREPARE_BENCHMARK = SUBMIT_BENCHMARK + 1;
  byte START_BENCHMARK = SUBMIT_BENCHMARK + 2;
  byte PRODUCER_FINISHED = SUBMIT_BENCHMARK + 3;
  byte END_BENCHMARK = SUBMIT_BENCHMARK + 4;
  byte PREPARE_RESPONSE = SUBMIT_BENCHMARK + 5;

  byte REPORT_STATS = 20;

  byte GET_CLIENT_INFO = 30;
  byte CLIENT_INFO = GET_CLIENT_INFO + 1;

  byte SHUTDOWN = Byte.MAX_VALUE;
}
