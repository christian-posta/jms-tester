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
package com.fusesource.forge.jmstest.peristence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.executor.ReleaseManager;
import com.fusesource.forge.jmstest.executor.Releaseable;
import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;
import com.fusesource.forge.jmstest.probe.Probe;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public abstract class CachingSamplePersistenceAdapter implements Releaseable,
    BenchmarkSamplePersistenceAdapter {

  public final static int DEFAULT_CACHE_SIZE = 100;

  private ClientId clientId;
  private boolean initialized = false;
  private int cacheSize = DEFAULT_CACHE_SIZE;

  private Map<ProbeDescriptor, Long> lastRecorded = null;
  private Map<ProbeDescriptor, String> dataSources = null;
  private TreeMap<Long, List<BenchmarkProbeValue>> valueCache;

  public CachingSamplePersistenceAdapter(ClientId clientId) {
    valueCache = new TreeMap<Long, List<BenchmarkProbeValue>>();
    this.clientId = clientId;
    lastRecorded = new HashMap<ProbeDescriptor, Long>();
  }

  public ClientId getClientId() {
    return clientId;
  }

  public void setClientId(ClientId clientId) {
    this.clientId = clientId;
  }

  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  public int getCacheSize() {
    return cacheSize;
  }

  public Map<ProbeDescriptor, String> getDataSources() {
    if (dataSources == null) {
      dataSources = new TreeMap<ProbeDescriptor, String>();
    }
    return dataSources;
  }

  public ProbeDescriptor getDescriptorByPhysicalName(String name) {
    ProbeDescriptor result = null;

    for (ProbeDescriptor pd : getDataSources().keySet()) {
      if (getDataSources().get(pd).equals(name)) {
        result = pd;
        break;
      }
    }
    return result;
  }

  public void record(BenchmarkProbeValue value) {

    if (!getDataSources().containsKey(value.getDescriptor())) {
      getDataSources().put(value.getDescriptor(), "" + getDataSources().size());
    }

    lastRecorded.put(value.getDescriptor(), new Long(value.getTimestamp()));

    synchronized (valueCache) {
      List<BenchmarkProbeValue> values = valueCache.get(value.getTimestamp());
      if (values == null) {
        values = new ArrayList<BenchmarkProbeValue>();
        valueCache.put(new Long(value.getTimestamp()), values);
      }
      values.add(value);

      if (valueCache.size() > getCacheSize()) {
        flushCache(false);
      }
    }
  }

  public void init() {
    ReleaseManager.getInstance().register(this);
  }

  synchronized public void start() {
    if (initialized) {
      return;
    }

    init();

    initialized = true;
  }

  public void release() {
    flushCache(true);
    ReleaseManager.getInstance().deregister(this);
  }

  public void stop() {
    release();
  }

  protected abstract void flushValues(List<BenchmarkProbeValue> sample);

  protected void startFlush() {
  }

  protected void finishFlush() {
  }

  final protected void flushCache(boolean flushCompletely) {
    synchronized (valueCache) {
      if (valueCache == null || valueCache.isEmpty()) {
        return;
      }

      startFlush();

      while (!valueCache.isEmpty()) {
        Long timeStamp = valueCache.firstKey();
        if (flushCompletely || isComplete(timeStamp)) {
          flushValues(valueCache.remove(valueCache.firstKey()));
        } else {
          break;
        }
      }

      finishFlush();
    }
  }

  protected boolean isComplete(Long timeStamp) {
    boolean result = true;

    for (ProbeDescriptor pd : getDataSources().keySet()) {
      Long recorded = lastRecorded.get(pd);
      if (recorded == null || recorded < timeStamp) {
        result = false;
        break;
      }
    }
    return result;
  }

  public void update(Observable o, Object arg) {
    if ((Object) o instanceof Probe) {
      if (arg instanceof BenchmarkProbeValue) {
        BenchmarkProbeValue value = (BenchmarkProbeValue) arg;
        record(value);
      }
    }
  }
}
