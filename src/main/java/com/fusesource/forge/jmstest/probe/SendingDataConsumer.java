package com.fusesource.forge.jmstest.probe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SendingDataConsumer extends AbstractProbeDataConsumer {
	
	private Log log = null;
	
	@Override
	public void record(long timestamp, Number value) {
		log().debug("Recording: " + getName() + ":" + timestamp + ":" + value);
		// TODO Auto-generated method stub
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
