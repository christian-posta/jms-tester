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
package com.fusesource.forge.jmstest.probe.sigar;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.probe.sigar.IOStat.IOStatType;

public class SigarTestManual {

  @BeforeTest
  public void addSigarLibs() {
    String libPath = System.getProperty("java.library.path")
        + File.pathSeparator + "src/main/lib";
    System.setProperty("java.library.path", libPath);
  }

  @Test
  public void testCpuStatProbe() {
    CpuStat cs = new CpuStat("CPU");
    Number n = cs.getValue();
    Assert.assertTrue(n.doubleValue() >= 0.0 && n.doubleValue() <= 100.0);
  }

  @Test
  public void testIOStatProbe() {
    IOStat is = new IOStat("IOStat");
    is.setType(IOStatType.DISK_READS);
    Number n = is.getValue();
    System.out.println(is.getType() + ":" + n);
  }
}
