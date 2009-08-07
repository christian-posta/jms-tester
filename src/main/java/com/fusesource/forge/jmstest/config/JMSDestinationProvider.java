/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.config;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

public interface JMSDestinationProvider {
	Destination getDestination(final Session _session, final String _destName) throws Exception;
	Topic getTopic(Session _session, String _topic) throws Exception;
	Queue getQueue(Session _session, String _queue) throws Exception;
}
