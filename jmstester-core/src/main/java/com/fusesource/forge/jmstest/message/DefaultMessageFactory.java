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
package com.fusesource.forge.jmstest.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class DefaultMessageFactory implements MessageFactory {

  private String prefix = null;
  private int msgSize = 1000;

  public void setMessageSize(int msgSize) {
    this.msgSize = msgSize;
  }

  public int getMessageSize() {
    return msgSize;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix != null ? prefix : "";
  }

  protected String getMessageText() {

    StringBuffer buffer = new StringBuffer(getMessageSize());
    buffer.append(getPrefix());
    buffer.append(getAdditionalContent());
    if (buffer.length() > getMessageSize()) {
      return buffer.substring(0, getMessageSize());
    }
    for (int i = buffer.length(); i < getMessageSize(); i++) {
      buffer.append(' ');
    }
    return buffer.toString();
  }

  protected String getAdditionalContent() {
    return "";
  }

  public Message createMessage(Session session) throws JMSException {
    return session.createTextMessage(getMessageText());
  }
}
