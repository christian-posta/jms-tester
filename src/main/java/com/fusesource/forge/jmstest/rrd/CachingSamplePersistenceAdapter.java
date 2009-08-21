package com.fusesource.forge.jmstest.rrd;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.executor.Releaseable;

public abstract class CachingSamplePersistenceAdapter implements Releaseable, BenchmarkSamplePersistenceAdapter {
	
	private Map<String, BenchmarkSampleRecorder> rrdRecorder;

	private ClientId clientId;
	private boolean initialized = false;
	private boolean autoStart = true;
	
	private ScheduledThreadPoolExecutor executor = null;
	private TreeMap<Long, BenchmarkSample> valueCache;
	
	public CachingSamplePersistenceAdapter(ClientId clientId) {
		valueCache = new TreeMap<Long, BenchmarkSample>();
		this.clientId = clientId;
	}

	public boolean isAutoStart() {
		return autoStart;
	}

	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}

	public ClientId getClientId() {
		return clientId;
	}

	public void setClientId(ClientId clientId) {
		this.clientId = clientId;
	}

	synchronized public Map<String, BenchmarkSampleRecorder> getRrdRecorder() {
		if (rrdRecorder == null) {
			rrdRecorder = new HashMap<String, BenchmarkSampleRecorder>();
		}
		return rrdRecorder;
	}
	
	synchronized public void addRecorder(BenchmarkSampleRecorder recorder) {
		if (!getRrdRecorder().containsKey(recorder.getName())) {
			getRrdRecorder().put(recorder.getName(), recorder);
		}
	}

	public void record(BenchmarkSampleRecorder recorder, long timestamp, Number value) {
		synchronized (valueCache) {
			if (!getRrdRecorder().containsKey(recorder.getName())) {
				addRecorder(recorder);
			}
			BenchmarkSample sample = valueCache.get(timestamp);
			if (sample == null) {
				sample = new BenchmarkSample(timestamp);
				sample.setDsType(recorder.getDsType());
				valueCache.put(new Long(timestamp), sample);
			}
			sample.setValue(recorder.getName(), value.doubleValue());
		}
	}

	public void init() {
		log().debug("Initializing RRDController ...");
		ReleaseManager.getInstance().register(this);
	}
	
	synchronized public void start() {
		
		if (initialized) {
			return;
		}
	
		init();
		
		executor = new ScheduledThreadPoolExecutor(1);
		executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				flushSamples(false);
			}
		}, 5, 5, TimeUnit.SECONDS);
		initialized = true;
		log().debug("RRDController initialization complete.");
	}
	
	public void release() {
		if (executor != null) {
			executor.shutdown();
		}
		flushSamples(true);
		ReleaseManager.getInstance().deregister(this);
	}
	
	public void stop() {
		release();
	}
	
	protected abstract void flushSample(BenchmarkSample sample);

	protected void startFlush() {}
	protected void finishFlush() {}
	
	final protected void flushSamples(boolean complete) {
		
		log().debug("Flushing Samples ...");
		
		synchronized (valueCache) {
			if (valueCache == null || valueCache.isEmpty()) {
				return;
			}

			startFlush();
			
			log().debug(valueCache.size() + " values in cache.");
			long currentTime = System.currentTimeMillis() / 1000;

			while((!valueCache.isEmpty()) && (complete || valueCache.firstKey() < currentTime)) {
				BenchmarkSample s = valueCache.remove(valueCache.firstKey());
				flushSample(s);
			}
			
			finishFlush();

			log().debug("Pushed Metrics to RRD backend; " + valueCache.size() + " values remaining in cache.");
		}
	}
	
	private Log log() {
		return LogFactory.getLog(this.getClass());
	}
}
