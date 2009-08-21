package com.fusesource.forge.jmstest.rrd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rrd4j.DsType;

import com.fusesource.forge.jmstest.probe.AbstractProbeDataConsumer;

public class BenchmarkSampleRecorderImpl extends AbstractProbeDataConsumer implements BenchmarkSampleRecorder {
	
	private BenchmarkSamplePersistenceAdapter adapter;
	private DsType dsType = DsType.COUNTER;
	
	public BenchmarkSamplePersistenceAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(BenchmarkSamplePersistenceAdapter adapter) {
		this.adapter = adapter;
		if (adapter != null) {
			adapter.addRecorder(this);
		}
	}

	public DsType getDsType() {
		return dsType;
	}

	public void setDsType(DsType dsType) {
		this.dsType = dsType;
	}
	
	public void record(Number value) {
		record(System.currentTimeMillis() / 1000, value);
	}
	
	public void record(long timestamp, Number value) {
		log().debug(toString() + " recording : " + timestamp + ":" + value);
		if (getAdapter() != null) {
			getAdapter().record(this, timestamp, value);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("RRDRecorderImpl[");
		buf.append(getName());
		buf.append(",");
		switch (getDsType()) {
		case ABSOLUTE:
			buf.append("ABSOLUTE");
			break;
		case COUNTER:
			buf.append("COUNTER");
			break;
		case GAUGE:
			buf.append("GAUGE");
			break;
		case DERIVE:
			buf.append("DERIVE");
			break;
		default:
			buf.append("UNKNOWN");
		}
		buf.append("]");
		return buf.toString();
	}
	
	private Log log() {
		return LogFactory.getLog(this.getClass());
	}
}
