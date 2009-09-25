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
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LimitedTimeScheduledExecutor implements Runnable, Releaseable {

  private String name;

  private long duration = 300;
  private long interval = 5;
  private Runnable task;

  private ScheduledThreadPoolExecutor executor;
  private Thread controlThread;

  private boolean isRunning = false;

  private Object lock = new Object();
  CountDownLatch latch;
  List<CountDownLatch> waiting;

  public LimitedTimeScheduledExecutor() {
    waiting = new ArrayList<CountDownLatch>();
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    if (name == null) {
      name = this.getClass().getName() + "-" + UUID.randomUUID();
    }
    return name;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public long getInterval() {
    return interval;
  }

  public void setInterval(long interval) {
    this.interval = interval;
  }

  public void setTask(Runnable task) {
    this.task = task;
  }

  public Runnable getTask() {
    return task;
  }

  public ReleaseManager getReleaseManager() {
    return ReleaseManager.getInstance();
  }

  public void start() {
    synchronized (lock) {
      if (isRunning) {
        return;
      }
      controlThread = new Thread(this, getName());
      controlThread.start();
      isRunning = true;
    }
  }

  public void waitUntilFinished() {
    synchronized (lock) {
      if (!isRunning) {
        return;
      }
    }
    CountDownLatch latch = new CountDownLatch(1);
    synchronized (waiting) {
      waiting.add(latch);
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
    }
  }

  public void run() {
    log().info(getClass().getSimpleName() + " " + getName() + " starting.");

    getReleaseManager().register(this);

    executor = new ScheduledThreadPoolExecutor(1);
    executor.scheduleAtFixedRate(getTask(), 0, getInterval(), TimeUnit.SECONDS);

    latch = new CountDownLatch(1);

    new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {

      public void run() {
        try {
          release();
        } catch (Exception e) {
        }
      }
    }, duration, TimeUnit.SECONDS);

    try {
      latch.await();
    } catch (InterruptedException ie) {
      try {
        release();
      } catch (Exception e) {
      }
    }
    isRunning = false;
    log().info(getClass().getSimpleName() + " " + getName() + " finished.");
  }

  public void release() {
    synchronized (lock) {
      if (!isRunning) {
        return;
      }
      if (executor != null) {
        log().debug("Closing " + getName());
        executor.shutdown();
        executor = null;
      }

      synchronized (waiting) {
        while (waiting.size() > 0) {
          CountDownLatch l = waiting.remove(0);
          l.countDown();
        }
      }
      latch.countDown();
      getReleaseManager().deregister(this);
    }
  }

  private Log log() {
    return LogFactory.getLog(this.getClass());
  }
}
