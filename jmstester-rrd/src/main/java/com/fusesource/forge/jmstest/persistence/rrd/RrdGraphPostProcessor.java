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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.Archive;
import org.rrd4j.core.RrdDb;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

public class RrdGraphPostProcessor extends AbstractRRDPostProcessor {

  private Log log = null;

  @Override
  public void processData() {
    super.processData();
    RrdDb db = getDatabase().getDatabase();

    try {
      for (int i = 0; i < db.getArcCount(); i++) {
        Archive arch = db.getArchive(i);
        for (String dsName : db.getDsNames()) {

          String probeName = getDatabase().getDescriptorByPhysicalName(dsName)
              .getName();

          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

          StringBuffer title = new StringBuffer(probeName);
          title.append(" ");
          title.append(sdf.format(new Date(arch.getStartTime() * 1000)));
          title.append("-");
          title.append(sdf.format(new Date(arch.getEndTime() * 1000)));

          log().info("Creating Graph: " + title);

          RrdGraphDef graphDef = new RrdGraphDef();

          graphDef.setTitle(title.toString());
          graphDef.setTimeSpan(arch.getStartTime(), arch.getEndTime());
          graphDef.datasource(dsName, getDbFileName(), dsName,
              ConsolFun.AVERAGE);
          graphDef.setStep(arch.getArcStep());
          graphDef.setWidth(800);
          graphDef.setHeight(600);
          graphDef.line(dsName, Color.BLUE, probeName, 2.0f);
          graphDef.setImageFormat("PNG");
          graphDef.setFilename(getWorkDir().getAbsolutePath() + "/" + probeName
              + ".png");
          RrdGraph graph = new RrdGraph(graphDef);
          BufferedImage bi = new BufferedImage(100, 100,
              BufferedImage.TYPE_INT_RGB);
          graph.render(bi.getGraphics());
        }
      }
    } catch (Exception e) {
      log().error("Error while generating graphs.");
    }
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
