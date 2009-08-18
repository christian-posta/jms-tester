/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.config;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JndiInitialContextFactory {
  private Properties jndiProperties_ = null;
  private Context    initialContext_ = null;

  public Context getInitialContext() throws NamingException {
    if (initialContext_ == null) {
      initialContext_ = new InitialContext(getJndiProperties());
    }
    return initialContext_;
  }

  public void setJndiProperties(final Map <String,String> _jndiPropertiesMap) {
    jndiProperties_ = new Properties();
    if (_jndiPropertiesMap != null) {
      for (final Iterator < String > it = _jndiPropertiesMap.keySet().iterator(); it.hasNext();) {
        final String key = it.next();
        final String value = _jndiPropertiesMap.get(key);
        jndiProperties_.put(key, value);
      }
    }
  }

  private Properties getJndiProperties() {
    return jndiProperties_;
  }
}
