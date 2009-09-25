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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;

public class SteppablePool {
  
  private ExecutorService executor = null;
  private List<Steppable> tasks = new ArrayList<Steppable>();
  private Runnable controller = null;
  private long idleTimeout = 5000;
  private long lastUnidle = System.currentTimeMillis();
  private List<CountDownLatch> latches = new ArrayList<CountDownLatch>();
  
  private Log log = null;
  
  public SteppablePool() {
    controller = new Runnable() {
      public void run() {
        log().info("Steppable pool is started.");
        while(true) {
          if (isIdle()) {
            if (System.currentTimeMillis() - lastUnidle > idleTimeout) {
              break;
            } else {
              try {
                Thread.sleep(idleTimeout);
              } catch (InterruptedException ie) {
                break;
              }
            }
          }
        }
        log().info("Steppable pool is finished.");
        for(CountDownLatch latch: latches) {
          latch.countDown();
        }
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
      executor = new ThreadPoolExecutor(10, 20, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }
    return executor;
  }
 
  synchronized public void submit(final Steppable s) {
    tasks.add(s);
    s.setPool(this);
    getExecutor().submit(new Runnable() {
      public void run() {
        s.initialize();
      }
    });
  }
  
  synchronized private boolean isIdle() {
    if (!tasks.isEmpty()) {
      List<Steppable> revisedTasks = new ArrayList<Steppable>();
      for(Steppable s: tasks) {
        if (s.getState() != SteppableState.RELEASED) {
          revisedTasks.add(s);
        }
      }
      tasks = revisedTasks;
    }
    if (!tasks.isEmpty()) {
      lastUnidle = System.currentTimeMillis();
    }
    return (tasks.isEmpty());
  }
  
  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
