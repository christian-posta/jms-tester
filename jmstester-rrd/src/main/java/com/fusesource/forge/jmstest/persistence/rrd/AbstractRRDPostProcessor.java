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
package com.fusesource.forge.jmstest.persistence.rrd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.executor.AbstractBenchmarkPostProcessor;
import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public abstract class AbstractRRDPostProcessor extends
    AbstractBenchmarkPostProcessor {

  private final static String RRD_FILE = "benchmark.rrd";
  private Log log = null;

  private Rrd4jSamplePersistenceAdapter rrdDatabase = null;

  protected String getDbFileName() {
    return new File(getWorkDir(), RRD_FILE).getAbsolutePath();
  }

  protected Rrd4jSamplePersistenceAdapter getDatabase() {

    File rrdFile = new File(getDbFileName());

    if (rrdDatabase == null || !rrdFile.exists()) {
      createRrdDatabase();
    }
    return rrdDatabase;
  }

  private void createRrdDatabase() {
    List<ProbeDescriptor> dataSources = new ArrayList<ProbeDescriptor>();
    dataSources.addAll(getDistinctProbes().values());
    rrdDatabase = new Rrd4jSamplePersistenceAdapter(dataSources);

    rrdDatabase.setFileName(getDbFileName());
    rrdDatabase.setStep(1);
    rrdDatabase.setArchiveLength((int) (getEndTime() - getStartTime() + 1));
    rrdDatabase.setStartTime(getStartTime() - 1);
    rrdDatabase.init();
  }

  private void recordData() {
    Rrd4jSamplePersistenceAdapter adapter = getDatabase();
    for (SampleIterator si = new SampleIterator(getWorkDir()); si.hasNext();) {
      BenchmarkProbeValue value = si.next();
      adapter.record(value);
    }
    rrdDatabase.release();
  }

  public void processData() {
    log().debug(
        "Processing data from directory: " + getWorkDir().getAbsolutePath());
    recordData();
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
