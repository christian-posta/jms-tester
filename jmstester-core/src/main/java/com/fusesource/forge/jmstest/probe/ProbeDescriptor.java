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

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue.ValueType;

public class ProbeDescriptor implements Serializable,
    Comparable<ProbeDescriptor> {

  private static final long serialVersionUID = 4408538380108054561L;

  private String name;
  private ValueType valueType;

  public ProbeDescriptor(Probe p) {
    this.name = p.getName();
    this.valueType = p.getValueType();
  }

  public String getName() {
    return name;
  }

  public ValueType getValueType() {
    return valueType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((valueType == null) ? 0 : valueType.hashCode());
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
    ProbeDescriptor other = (ProbeDescriptor) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (valueType == null) {
      if (other.valueType != null)
        return false;
    } else if (!valueType.equals(other.valueType))
      return false;
    return true;
  }

  public int compareTo(ProbeDescriptor other) {
    return this.name.compareTo(other.getName());
  }

  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer("ProbeDescriptor[");
    buf.append(getName());
    buf.append(",");
    buf.append(getValueType());
    buf.append("]");
    return buf.toString();
  }
}
