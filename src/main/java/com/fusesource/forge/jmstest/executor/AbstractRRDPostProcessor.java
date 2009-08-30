package com.fusesource.forge.jmstest.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;
import com.fusesource.forge.jmstest.rrd.Rrd4jSamplePersistenceAdapter;

public class AbstractRRDPostProcessor extends  AbstractBenchmarkPostProcessor {
	
	private final static String RRD_FILE = "benchmark.rrd";
	private Log log = null;

	private Rrd4jSamplePersistenceAdapter rrdDatabase = null;
	
	protected String getDbFileName() {
		return new File(getWorkDir(), RRD_FILE).getAbsolutePath();
	}
	
	protected Rrd4jSamplePersistenceAdapter getDatabase() {
		
		File rrdFile = new File(getDbFileName());
		
		if (rrdDatabase == null  || !rrdFile.exists()) {
			createRrdDatabase();
		}
		return rrdDatabase;
	}

	private void createRrdDatabase() {
		List<ProbeDescriptor> dataSources = new ArrayList<ProbeDescriptor>();
		dataSources.addAll(getDistinctProbes().values());
		rrdDatabase = new Rrd4jSamplePersistenceAdapter(dataSources);

		rrdDatabase.setFileName(getDbFileName());
		rrdDatabase.setStep(1);
		rrdDatabase.setArchiveLength((int)(getEndTime() - getStartTime() + 1));
		rrdDatabase.setStartTime(getStartTime() - 1);
		rrdDatabase.init();
	}
	
	private void recordData() {
		Rrd4jSamplePersistenceAdapter adapter = getDatabase();
		for(SampleIterator si = new SampleIterator(getWorkDir()); si.hasNext();) {
			BenchmarkProbeValue value = si.next();
			adapter.record(value);
		}
		rrdDatabase.release();
	}
	
	public void processData() {
		log().debug("Processing data from directory: " + getWorkDir().getAbsolutePath());
		recordData();
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		} 
		return log;
	}
}
