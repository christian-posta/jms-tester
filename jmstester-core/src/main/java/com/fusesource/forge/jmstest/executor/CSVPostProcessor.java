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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.peristence.CSVSamplePersistenceAdapter;
import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public class CSVPostProcessor extends AbstractBenchmarkPostProcessor {

  private final static String CSV_FILE = "benchmark.csv";
  private Log log = null;

  private CSVSamplePersistenceAdapter csvStore = null;

  protected String getCSVFileName() {
    return new File(getWorkDir(), CSV_FILE).getAbsolutePath();
  }

  protected CSVSamplePersistenceAdapter createCSVFile() {
    File csvFile = new File(getCSVFileName());

    if (csvStore == null || !csvFile.exists()) {
      List<ProbeDescriptor> dataSources = new ArrayList<ProbeDescriptor>();
      dataSources.addAll(getDistinctProbes().values());
      csvStore = new CSVSamplePersistenceAdapter(dataSources);

      csvStore.setFileName(getCSVFileName());
      csvStore.setStartTime(getStartTime() - 1);
      csvStore.init();
    }
    return csvStore;
  }

  private void recordData() {
    CSVSamplePersistenceAdapter adapter = createCSVFile();
    for (SampleIterator si = new SampleIterator(getWorkDir()); si.hasNext();) {
      BenchmarkProbeValue value = si.next();
      adapter.record(value);
    }
    adapter.release();
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
