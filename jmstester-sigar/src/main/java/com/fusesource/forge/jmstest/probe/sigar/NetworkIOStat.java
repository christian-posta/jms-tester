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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.SigarException;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue.ValueType;

public class NetworkIOStat extends AbstractSigarProbe {

  private NetworkIOStatType type = NetworkIOStatType.RX_BYTES;
  private Log log = null;

  public NetworkIOStat() {
    super();
  }

  public NetworkIOStat(String name) {
    super(name);
  }

  public void setType(NetworkIOStatType type) {
    this.type = type;
  }

  public NetworkIOStatType getType() {
    return type;
  }

  @Override
  public ValueType getValueType() {
    return ValueType.COUNTER;
  }

  @Override
  protected Number getValue() {

    long result = 0;

    try {
      for (String ifName : getSigar().getNetInterfaceList()) {
        NetInterfaceStat stat = getSigar().getNetInterfaceStat(ifName);
        switch (getType()) {
        case RX_BYTES:
          result += stat.getRxBytes();
          break;
        case RX_PACKETS:
          result += stat.getRxPackets();
          break;
        case TX_BYTES:
          result += stat.getTxBytes();
          break;
        case TX_PACKETS:
          result += stat.getTxPackets();
          break;
        case RX_DROPPED:
          result += stat.getRxDropped();
          break;
        case TX_DROPPED:
          result += stat.getTxDropped();
          break;
        case RX_ERRORS:
          result += stat.getRxErrors();
          break;
        case TX_ERRORS:
          result += stat.getTxErrors();
          break;
        default:
          // do nothing
        }
      }
    } catch (SigarException e) {
      log().error("Unable to retrieve network statistics.", e);
    }
    return result;
  }

  private Log log() {
    if (log == null) {
      log = LogFactory.getLog(this.getClass());
    }
    return log;
  }

  public enum NetworkIOStatType {
    RX_BYTES, RX_PACKETS, TX_BYTES, TX_PACKETS, RX_DROPPED, TX_DROPPED, RX_ERRORS, TX_ERRORS
  }
}
