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
package com.fusesource.forge.jmstest.config;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringConfigHelper {

  private String baseDir = null;
  private List<String> springConfigLocations;
  private ApplicationContext applicationContext = null;

  private Log log = null;

  public SpringConfigHelper() {
    springConfigLocations = new ArrayList<String>();
  }

  public void setSpringConfigLocations(List<String> locations) {
    springConfigLocations = new ArrayList<String>();

    for (String location : locations) {
      File f = new File(getBaseDir(), location);
      if (f.exists()) {
        if (f.canRead()) {
          if (f.isDirectory()) {
            for (String fileName : f.list(new FilenameFilter() {
              public boolean accept(File dir, String name) {
                File candidate = new File(dir, name);
                if (!candidate.isFile()) {
                  return false;
                } else {
                  return name.endsWith(".xml");
                }
              }
            })) {
              String absFileName = new File(f, fileName).getAbsolutePath();
              log().debug("Found xml file: " + absFileName);
              springConfigLocations.add(absFileName);
            }
          } else if (f.isFile()) {
            if (location.endsWith(".xml")) {
              String absFileName = f.getAbsolutePath();
              log().debug("Found xml file: " + absFileName);
              springConfigLocations.add(absFileName);
            }
          }
        }
      }
    }

    applicationContext = null;
  }

  public void setSpringConfigLocations(String locationString) {
    List<String> locations = new ArrayList<String>();
    StringTokenizer sTok = new StringTokenizer(locationString, ":");

    while (sTok.hasMoreTokens()) {
      locations.add(sTok.nextToken());
    }

    setSpringConfigLocations(locations);

  }

  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }

  private File getBaseDir() {
    if (baseDir == null) {
      baseDir = System.getProperty("user.dir");
    }
    return new File(baseDir);
  }

  public List<String> getSpringConfigLocations() {
    return this.springConfigLocations;
  }

  public ApplicationContext getApplicationContext() {

    if (applicationContext == null) {
      String[] configLocations = new String[getSpringConfigLocations().size()];
      int i = 0;
      for (String location : getSpringConfigLocations()) {
        configLocations[i++] = "file://" + location;
      }

      try {
        applicationContext = new FileSystemXmlApplicationContext(
            configLocations);
      } catch (BeansException be) {
        log().error("Could not create Application Context.", be);
      }
      if (log().isDebugEnabled()) {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
          log().debug("Found bean: " + beanName);
        }
      }
    }
    return applicationContext;
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }
}
