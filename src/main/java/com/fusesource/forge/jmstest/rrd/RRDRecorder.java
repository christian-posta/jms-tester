package com.fusesource.forge.jmstest.rrd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rrd4j.DsType;

import com.fusesource.forge.jmstest.probe.AbstractProbeDataConsumer;

public class RRDRecorder extends AbstractProbeDataConsumer {
	
	private RRDController controller;
	private DsType dsType = DsType.COUNTER;
	
	public RRDRecorder() {}

	public RRDController getController() {
		return controller;
	}

	public void setController(RRDController controller) {
		this.controller = controller;
		controller.addRrdRecorder(this);
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
		if (getController() != null) {
			getController().record(this, timestamp, value);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("RRDRecorder[");
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
