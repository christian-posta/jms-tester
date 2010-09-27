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
package com.fusesource.forge.jmstest.threading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SteppablePool {
  
  private ExecutorService executor = null;
  private Map<String, Steppable> tasks = new HashMap<String, Steppable>();
  private Runnable controller = null;
  private long idleTimeout = 5000;
  private int minPoolSize = 10;
  private int maxPoolSize = 20;
  private long lastUnidle = System.currentTimeMillis();
  private long lastCount = Long.MIN_VALUE;
  private List<CountDownLatch> latches = new ArrayList<CountDownLatch>();
  
  private Log log = null;
  
  public SteppablePool(long idleTimeout, int minPoolSize, int maxPoolSize) {
    this.idleTimeout = idleTimeout;
    this.minPoolSize = minPoolSize;
    this.maxPoolSize = maxPoolSize;
    
    controller = new Runnable() {
      public void run() {
        log().info("Steppable pool is started.");
        while(true) {
          if (isIdle()) {
            if (System.currentTimeMillis() - lastUnidle > getIdleTimeout()) {
              break;
            }
          } else {
            log.debug("Steppable pool is still busy.");
          }
          try {
            Thread.sleep(getIdleTimeout());
          } catch (InterruptedException ie) {
            break;
          }
        }
        log().info("Steppable pool is finished.");
        for(CountDownLatch latch: latches) {
          latch.countDown();
        }
        executor.shutdown();
      }
    };
    new Thread(controller).start();
  }
  
  synchronized public void touch() {
    lastUnidle = System.currentTimeMillis();
  }
  
  public void waitUntilFinished() {
    CountDownLatch latch = new CountDownLatch(1);
    latches.add(latch);
    try {
      latch.await();
    } catch (InterruptedException ie) {
    }
  }
  
  public ExecutorService getExecutor() {
    if (executor == null) {
      executor = new ThreadPoolExecutor(minPoolSize, maxPoolSize, getIdleTimeout(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }
    return executor;
  }
 
  public void remove(final Steppable s) {
    synchronized (tasks) {
      if (s.getState() == SteppableState.RELEASED) {
        tasks.remove(s.getName());
      }
    }
  }
  
  public void submit(final Steppable s) {
    synchronized(tasks) {
      tasks.put(s.getName(), s);
    }
    s.setPool(this);
    getExecutor().submit(new Runnable() {
      public void run() {
        s.initialize();
      }
    });
  }
  
  private boolean isIdle() {
    synchronized(tasks) {
      if (tasks.size() > 0) {
        lastUnidle = System.currentTimeMillis();
      }
      log().info("Pool still has " + (tasks.size()) + " tasks.");
      if (tasks.size() == lastCount) {
        for(Steppable s: tasks.values()) {
          log.info("Task : " + s.getName() + ":" + s.getState());
        }
      } else {
        lastCount = tasks.size();
      }
      return (tasks.size() <= 0);
    }
  }
  
  private long getIdleTimeout() {
    return idleTimeout;
  }
  
  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
