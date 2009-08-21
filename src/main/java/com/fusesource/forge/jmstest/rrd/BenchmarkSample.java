package com.fusesource.forge.jmstest.rrd;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.rrd4j.DsType;

public class BenchmarkSample implements Serializable {
	
	private static final long serialVersionUID = 8063590426890302452L;

	private DsType dsType;
	private long timestamp;
	private Map<String, Number> values;
	
	public BenchmarkSample() {
		this(System.currentTimeMillis() / 1000);
	}
	
	public DsType getDsType() {
		return dsType;
	}

	public void setDsType(DsType dsType) {
		this.dsType = dsType;
	}


	public BenchmarkSample(long timestamp) {
		this.timestamp = timestamp;
		values = new HashMap<String, Number>();
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public Map<String, Number> getValues() {
		return values;
	}
	
	public void setValues(Map<String, Number> values) {
		this.values = values;
	}
	
	public void setValue(String name, Number value) {
		getValues().put(name, value);
	}
}
