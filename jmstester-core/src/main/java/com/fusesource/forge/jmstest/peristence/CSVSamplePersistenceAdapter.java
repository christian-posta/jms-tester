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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public class CSVSamplePersistenceAdapter extends ProbeAwarePeristenceAdapter {

  private OutputStream os = null;
  private Writer writer = null;

  public CSVSamplePersistenceAdapter(List<ProbeDescriptor> descriptors) {
    super(descriptors);
  }

  @Override
  protected void startFlush() {
    super.startFlush();
    openCSVFile();
  }

  @Override
  protected void finishFlush() {
    closeCSVFile();
    super.finishFlush();
  }

  private void openCSVFile() {
    try {
      os = new FileOutputStream(getFileName(), true);
      writer = new PrintWriter(os);
    } catch (IOException ioe) {
      log().error("Could not open file to write: " + getFileName(), ioe);
      os = null;
      writer = null;
    }
  }

  private void closeCSVFile() {
    try {
      writer.close();
      os.flush();
      os.close();
    } catch (IOException ioe) {
      log().warn("Could not close file after write: " + getFileName(), ioe);
    } finally {
      writer = null;
      os = null;
    }
  }

  @Override
  public void init() {
    super.init();

    openCSVFile();

    try {
      if (writer != null) {
        writer.write("Timestamp,Date");
        for (ProbeDescriptor pd : getDataSources().keySet()) {
          writer.write("," + pd.getName());
        }
        writer.write("\n");
      }
    } catch (IOException ioe) {
      log().error("Error writing CSV Header to " + getFileName(), ioe);
    }

    closeCSVFile();
  }

  @Override
  public void release() {
    super.release();
    log().info("Written probe Values to : " + getFileName());
  }

  @Override
  protected void flushValues(List<BenchmarkProbeValue> values) {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    Map<ProbeDescriptor, BenchmarkProbeValue> valueMap = new TreeMap<ProbeDescriptor, BenchmarkProbeValue>();

    for (BenchmarkProbeValue value : values) {
      valueMap.put(value.getDescriptor(), value);
    }

    if (writer != null) {
      try {
        if (values.size() > 0) {
          long timestamp = values.get(0).getTimestamp();
          writer.write("" + timestamp + ",");
          writer.write(sdf.format(new Date(timestamp * 1000)));
          for (ProbeDescriptor pd : getDataSources().keySet()) {
            writer.write(",");
            if (valueMap.containsKey(pd)) {
              writer.write("" + valueMap.get(pd).getValue().toString());
            }
          }
          writer.write("\n");
        }
      } catch (IOException e) {
        log().error("Error writing sample to file: " + values.toString(), e);
      }
    }
  }

  private Log log() {
    return LogFactory.getLog(this.getClass());
  }
}
