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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public abstract class ProbeAwarePeristenceAdapter extends
    CachingSamplePersistenceAdapter {

  private String fileName = "jmstest.rrd";
  private Long startTime = null;

  private Log log = null;

  public ProbeAwarePeristenceAdapter(List<ProbeDescriptor> descriptors) {
    super(null);

    for (int i = 0; i < descriptors.size(); i++) {
      ProbeDescriptor pd = descriptors.get(i);
      getDataSources().put(pd, "" + i);
    }
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setStartTime(long startTime) {
    this.startTime = new Long(startTime);
  }

  public long getStartTime() {
    if (startTime == null) {
      startTime = new Long(System.currentTimeMillis() / 1000);
    }
    return startTime.longValue();
  }

  public void init() {
    super.init();

    File dbFile = new File(getFileName());

    if (!dbFile.getAbsoluteFile().getParentFile().exists()) {
      try {
        dbFile.getCanonicalFile().mkdirs();
      } catch (IOException e) {
        log().error("Error creating Rrd4j file.", e);
      }
    }

    if (dbFile.exists()) {
      dbFile.delete();
    }

    log().debug("Persistence Adapter uses : " + dbFile.getAbsolutePath());
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
