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
package com.fusesource.forge.jmstest.config;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

public interface JMSDestinationProvider {

  public final static String DEFAULT_BEAN_NAME = "destinationProvider";

  public Destination getDestination(
    final Session _session, final String _destName
  ) throws Exception;

  public Topic getTopic(Session _session, String _topic) throws Exception;

  public Queue getQueue(Session _session, String _queue) throws Exception;
}
