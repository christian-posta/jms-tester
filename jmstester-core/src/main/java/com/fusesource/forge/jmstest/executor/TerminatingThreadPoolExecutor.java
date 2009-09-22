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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TerminatingThreadPoolExecutor extends ThreadPoolExecutor {
  private AtomicLong lastSubmit = new AtomicLong(System.currentTimeMillis());
  private ScheduledThreadPoolExecutor scheduledChecker = null;

  private String name;

  private Log log = null;

  public TerminatingThreadPoolExecutor(String name, int corePoolSize,
      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    init(name);
  }

  public TerminatingThreadPoolExecutor(String name, int corePoolSize,
      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        handler);
    init(name);
  }

  public TerminatingThreadPoolExecutor(String name, int corePoolSize,
      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
      RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        threadFactory, handler);
    init(name);
  }

  public TerminatingThreadPoolExecutor(String name, int corePoolSize,
      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
        threadFactory);
    init(name);
  }

  private void init(String name) {
    setName(name);
    startChecker();
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    lastSubmit.set(System.currentTimeMillis());
    return super.submit(task);
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    lastSubmit.set(System.currentTimeMillis());
    return super.submit(task, result);
  }

  @Override
  public Future<?> submit(Runnable task) {
    lastSubmit.set(System.currentTimeMillis());
    return super.submit(task);
  }

  private void startChecker() {
    scheduledChecker = new ScheduledThreadPoolExecutor(1);
    scheduledChecker.scheduleAtFixedRate(
        new Runnable() {

          public void run() {
            log().debug("Checking Terminate condition for " + getName());
            long currentTime = System.currentTimeMillis();
            if (getActiveCount() == 0) {
              if (currentTime - lastSubmit.get() >= getKeepAliveTime(TimeUnit.MILLISECONDS)) {
                log().debug("Shutting down...");
                shutdown();
              }
            }
          }
        }, getKeepAliveTime(TimeUnit.NANOSECONDS),
        getKeepAliveTime(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS);

  }

  public String getName() {
    if (name == null) {
      name = this.getClass().getName();
    }
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void shutdown() {
    if (scheduledChecker != null) {
      scheduledChecker.shutdown();
    }
    super.shutdown();
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }

}
