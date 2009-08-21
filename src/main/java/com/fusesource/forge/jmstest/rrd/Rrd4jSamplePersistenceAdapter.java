package com.fusesource.forge.jmstest.rrd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;

import com.fusesource.forge.jmstest.benchmark.command.ClientId;

public class Rrd4jSamplePersistenceAdapter extends CachingSamplePersistenceAdapter {
	
	private String fileName = "jmstest.rrd"; 
	private long   step = 300L;
	private int archiveLength = 100;

	private RrdDb database;
	
	//TODO
	public Rrd4jSamplePersistenceAdapter() {
		super(null);
	}
	
	public Rrd4jSamplePersistenceAdapter(ClientId clientId) {
		super(clientId);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getStep() {
		return step;
	}

	public void setStep(long step) {
		this.step = step;
	}
	
	public int getArchiveLength() {
		return archiveLength;
	}

	public void setArchiveLength(int archiveLength) {
		this.archiveLength = archiveLength;
	}
	
	public RrdDb getDatabase() {
		if (database == null || database.isClosed()) {
			try {
				database = new RrdDb(getFileName());
			} catch (IOException e) {
			}
		}
		return database;
	}

	@Override
	protected void startFlush() {
		super.startFlush();
		getDatabase();
	}
	
	@Override
	protected void finishFlush() {
		super.finishFlush();
		try {
			getDatabase().close();
		} catch (IOException e) {
			log().error("Error closing Rrd database ...");
		}
	}

	public void init() {
		init(System.currentTimeMillis() / 1000);
	}
	
	public void init(long startTime) {
		super.init();
		
		File dbFile = new File(getFileName());
		
		log().debug("Setting Start time to : " + startTime);

		if (!dbFile.getAbsoluteFile().getParentFile().exists()) {
			try {
				dbFile.getCanonicalFile().mkdirs();
			} catch (IOException e) {
				log().error("Error creating Rrd4j file.", e);
			}
		}
		
		if(dbFile.exists()) {
			dbFile.delete();
		}
		
		log().debug("Rrd4j Adapter uses : " + dbFile.getAbsolutePath() + " Step: " + getStep());
		
		RrdDef rrdDef = new RrdDef(getFileName(), startTime, getStep());

		Collection<BenchmarkSampleRecorder> recorders = new ArrayList<BenchmarkSampleRecorder>();
		recorders.addAll(getRrdRecorder().values());
		getRrdRecorder().clear();
		
		for(BenchmarkSampleRecorder recorder: recorders) {
			log().debug("Adding Data Source to RRD: " + recorder);
			rrdDef.addDatasource(recorder.getName(), recorder.getDsType(), getStep(), Double.NaN, Double.NaN);
			getRrdRecorder().put(recorder.getName(), recorder);
		}
		
		rrdDef.setStartTime(startTime);
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 1, getArchiveLength());
		
		try {
			database = new RrdDb(rrdDef);
			database.close();
		} catch (IOException e) {
			log().error("Error creating RRD4J storage: ", e);
		}
	}
	
	@Override
	protected void flushSample(BenchmarkSample sample) {
		RrdDb db = getDatabase();
		Sample writeSample;
		
		try {
			writeSample = db.createSample();
			writeSample.setTime(sample.getTimestamp());
			for(String key: sample.getValues().keySet()) {
				log().debug("Writing Sample : " + writeSample.dump());
				writeSample.setValue(key, sample.getValues().get(key).doubleValue());
			}
			writeSample.update();
		} catch (Exception e) {
			log().error("Error flusging sample.", e);
		}
	}
	
	public void release() {
		try {
			if (database != null) {
				database.close();
			}
		} catch (IOException e) {}
		super.release();
	}
	
	private Log log() {
		return LogFactory.getLog(this.getClass());
	}
}
