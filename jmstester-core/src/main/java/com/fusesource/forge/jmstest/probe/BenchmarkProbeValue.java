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
package com.fusesource.forge.jmstest.probe;

import java.io.Serializable;

public class BenchmarkProbeValue implements Serializable {

  private static final long serialVersionUID = 6717825317880213002L;

  public enum ValueType {
    COUNTER, GAUGE
  }

  private long timestamp;
  private ProbeDescriptor descriptor;
  private Number value;

  public BenchmarkProbeValue(ProbeDescriptor descriptor, long timestamp,
      Number value) {
    this.descriptor = descriptor;
    this.timestamp = timestamp;
    this.value = value;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public Number getValue() {
    return value;
  }

  public ProbeDescriptor getDescriptor() {
    return descriptor;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((descriptor == null) ? 0 : descriptor.hashCode());
    result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BenchmarkProbeValue other = (BenchmarkProbeValue) obj;
    if (descriptor == null) {
      if (other.descriptor != null)
        return false;
    } else if (!descriptor.equals(other.descriptor))
      return false;
    if (timestamp != other.timestamp)
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "BenchmarkProbeValue [descriptor=" + descriptor + ", timestamp="
        + timestamp + ", value=" + value + "]";
  }
}
