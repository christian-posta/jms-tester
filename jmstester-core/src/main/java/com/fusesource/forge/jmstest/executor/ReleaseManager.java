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
package com.fusesource.forge.jmstest.executor;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a utility class where resources with cleanup requirements can be registered.
 * An instance of this class needs to be registered as a shutdown hook within the JVM. Each
 * registered object must implement the <code>Releasable</code> interface. Once the
 * shutdown hook is triggered, the <code>release()</code> method of all releasables
 * will be executed.
 *
 * @author andreasgies
 * @see Releaseable
 */
public class ReleaseManager extends Thread {

  private transient Log log;
  private Vector<Releaseable> releaseables;
  private static ReleaseManager instance = null;

  synchronized static public ReleaseManager getInstance() {
    if (instance == null) {
      instance = new ReleaseManager();
      Runtime.getRuntime().addShutdownHook(instance);
    }
    return instance;
  }

  private ReleaseManager() {
    releaseables = new Vector<Releaseable>();
  }

  public void register(Releaseable releaseable) {
    synchronized (releaseables) {
      releaseables.add(releaseable);
    }
  }

  public void deregister(Releaseable releaseable) {
    synchronized (releaseable) {
      releaseables.remove(releaseable);
    }
  }

  @Override
  public void run() {
    log().info("ShutdownHook called, attempting to sweep up resources");

    synchronized (releaseables) {
      while (releaseables != null && releaseables.size() > 0) {
        Releaseable r = releaseables.remove(0);
        r.release();
      }
    }
  }

  protected Log log() {
    if (log == null) {
      log = LogFactory.getLog(getClass());
    }
    return log;
  }

  static {
    ReleaseManager.getInstance();
  }
}
