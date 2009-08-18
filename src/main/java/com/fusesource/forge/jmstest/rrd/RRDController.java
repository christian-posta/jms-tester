package com.fusesource.forge.jmstest.rrd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;

import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.executor.Releaseable;

public class RRDController implements Releaseable {
	
	private String fileName = "jmstest.rrd"; 
	private long   step = 300L;
	private int archiveLength = 100;
	private Map<String, RRDRecorder> rrdRecorder;

	private boolean initialized = false;
	private RrdDb database;
	
	private boolean autoStart = true;
	
	private ScheduledThreadPoolExecutor executor = null;
	private TreeMap<Long, Sample> valueCache;
	
	public RRDController() {
		valueCache = new TreeMap<Long, Sample>();
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
	
	public boolean isAutoStart() {
		return autoStart;
	}

	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}

	public ReleaseManager getReleaseManager() {
		return ReleaseManager.getInstance();
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
	
	synchronized public Map<String, RRDRecorder> getRrdRecorder() {
		if (rrdRecorder == null) {
			rrdRecorder = new HashMap<String, RRDRecorder>();
		}
		return rrdRecorder;
	}
	
	synchronized public void addRrdRecorder(RRDRecorder recorder) {
		if (!getRrdRecorder().containsKey(recorder.getName())) {
			getRrdRecorder().put(recorder.getName(), recorder);
		}
	}

	public void record(RRDRecorder recorder, long timestamp, Number value) {
		synchronized (valueCache) {
			if (getRrdRecorder().containsKey(recorder.getName())) {
				try {
					Sample sample = valueCache.get(timestamp);
					if (sample == null) {
						sample = database.createSample();
						sample.setTime(timestamp);
						valueCache.put(new Long(timestamp), sample);
					}
					sample.setValue(recorder.getName(), value.doubleValue());
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			} else {
				log().warn("Ignoring recording for unregistered recorder: " + recorder.toString());
			}
		}
	}
	
	synchronized public void start() throws Exception {
		
		if (initialized) {
			return;
		}
		
		getReleaseManager().register(this);
		
		log().debug("Initializing RRDController ...");
		File dbFile = new File(getFileName());
		
		long startTime = System.currentTimeMillis() / 1000 - 1;
		log().debug("Setting Start time to : " + startTime);

		if (!dbFile.getAbsoluteFile().getParentFile().exists()) {
			dbFile.getCanonicalFile().mkdirs();
		}
		
		if(dbFile.exists()) {
			dbFile.delete();
		}
		
		log().debug("RRDController uses : " + dbFile.getAbsolutePath() + " Step: " + getStep());
		
		RrdDef rrdDef = new RrdDef(getFileName(), startTime, getStep());

		Collection<RRDRecorder> recorders = new ArrayList<RRDRecorder>();
		recorders.addAll(getRrdRecorder().values());
		getRrdRecorder().clear();
		
		for(RRDRecorder recorder: recorders) {
			log().debug("Adding Data Source to RRD: " + recorder);
			rrdDef.addDatasource(recorder.getName(), recorder.getDsType(), getStep(), Double.NaN, Double.NaN);
			getRrdRecorder().put(recorder.getName(), recorder);
		}
		
		rrdDef.setStartTime(startTime);
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 1, getArchiveLength());
		database = new RrdDb(rrdDef);
		database.close();
		
		executor = new ScheduledThreadPoolExecutor(1);
		executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				flushToRrd(false);
			}
		}, 5, 5, TimeUnit.SECONDS);
		initialized = true;
		log().debug("RRDController initialization complete.");
	}
	
	public void release() {
		if (executor != null) {
			executor.shutdown();
		}
		flushToRrd(true);
		try {
			if (database != null) {
				database.close();
			}
		} catch (IOException e) {}
		getReleaseManager().deregister(this);
	}
	
	private void flushToRrd(boolean complete) {
		log().debug("Pushing collected metrics to RRD database ...");
		synchronized (valueCache) {
			if (valueCache == null || valueCache.isEmpty()) {
				return;
			}

			log().debug(valueCache.size() + " values in cache.");
			long currentTime = System.currentTimeMillis() / 1000;

			RrdDb db = getDatabase();
			Sample writeSample;
			
			while((!valueCache.isEmpty()) && (complete || valueCache.firstKey() < currentTime)) {
				Sample s = valueCache.remove(valueCache.firstKey());
				try {
					writeSample = db.createSample();
					writeSample.setTime(s.getTime());
					writeSample.setValues(s.getValues());
					log().debug("Writing Sample : " + writeSample.dump());
					writeSample.update();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				db.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			log().debug("Pushed Metrics to RRD backend; " + valueCache.size() + " values remaining in cache.");
		}
	}
	
	private Log log() {
		return LogFactory.getLog(this.getClass());
	}
}
