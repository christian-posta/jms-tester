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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JndiInitialContextFactory {
  private Properties jndiProperties = null;
  private Context initialContext = null;

  public Context getInitialContext() throws NamingException {
    if (initialContext == null) {
      initialContext = new InitialContext(getJndiProperties());
    }
    return initialContext;
  }

  public void setJndiProperties(final Map<String, String> _jndiPropertiesMap) {
    jndiProperties = new Properties();
    if (_jndiPropertiesMap != null) {
      for (final Iterator<String> it = _jndiPropertiesMap.keySet().iterator(); it
          .hasNext();) {
        final String key = it.next();
        final String value = _jndiPropertiesMap.get(key);
        jndiProperties.put(key, value);
      }
    }
  }

  private Properties getJndiProperties() {
    return jndiProperties;
  }
}
