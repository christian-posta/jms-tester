package com.fusesource.forge.jmstest.rrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.executor.ReleaseManager;
import com.fusesource.forge.jmstest.executor.Releaseable;
import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;
import com.fusesource.forge.jmstest.probe.Probe;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public abstract class CachingSamplePersistenceAdapter implements Releaseable, BenchmarkSamplePersistenceAdapter {
	
	public final static int DEFAULT_CACHE_SIZE = 100;
	
	private ClientId clientId;
	private boolean initialized = false;
	private int cacheSize = DEFAULT_CACHE_SIZE;
	
	private Map<ProbeDescriptor, Long> lastRecorded = null;
	private Map<ProbeDescriptor, String> dataSources = null;
	private TreeMap<Long, List<BenchmarkProbeValue>> valueCache;
	
	public CachingSamplePersistenceAdapter(ClientId clientId) {
		valueCache = new TreeMap<Long, List<BenchmarkProbeValue>>();
		this.clientId = clientId;
		lastRecorded = new HashMap<ProbeDescriptor, Long>();
	}

	public ClientId getClientId() {
		return clientId;
	}

	public void setClientId(ClientId clientId) {
		this.clientId = clientId;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	public int getCacheSize() {
		return cacheSize;
	}

	public Map<ProbeDescriptor, String> getDataSources() {
		if (dataSources == null) {
			dataSources = new TreeMap<ProbeDescriptor, String>();
		}
		return dataSources;
	}
	
	public ProbeDescriptor getDescriptorByPhysicalName(String name) {
		ProbeDescriptor result = null;
		
		for (ProbeDescriptor pd: getDataSources().keySet()) {
			if (getDataSources().get(pd).equals(name)) {
				result = pd;
				break;
			}
		}
		return result;
	}
	
	public void record(BenchmarkProbeValue value) {
		
		if (!getDataSources().containsKey(value.getDescriptor())) { 
			getDataSources().put(value.getDescriptor(), "" + getDataSources().size());
		}
	
		lastRecorded.put(value.getDescriptor(), new Long(value.getTimestamp()));
		
		synchronized (valueCache) {
			List<BenchmarkProbeValue> values = valueCache.get(value.getTimestamp());
			if (values == null) {
				values = new ArrayList<BenchmarkProbeValue>();
				valueCache.put(new Long(value.getTimestamp()), values);
			}
			values.add(value);
			
			if (valueCache.size() > getCacheSize()) {
				flushCache(false);
			}
		}
	}

	public void init() {
		ReleaseManager.getInstance().register(this);
	}
	
	synchronized public void start() {
		if (initialized) {
			return;
		}
	
		init();
		
		initialized = true;
	}
	
	public void release() {
		flushCache(true);
		ReleaseManager.getInstance().deregister(this);
	}
	
	public void stop() {
		release();
	}
	
	protected abstract void flushValues(List<BenchmarkProbeValue> sample);

	protected void startFlush() {}
	protected void finishFlush() {}
	
	final protected void flushCache(boolean flushCompletely) {
		synchronized (valueCache) {
			if (valueCache == null || valueCache.isEmpty()) {
				return;
			}

			startFlush();
			
			while(!valueCache.isEmpty()) {
				Long timeStamp = valueCache.firstKey();
				if (flushCompletely || isComplete(timeStamp)) {
					flushValues(valueCache.remove(valueCache.firstKey()));
				} else {
					break;
				}
			}
			
			finishFlush();
		}
	}

	protected boolean isComplete(Long timeStamp) {
		boolean result = true;
		
		for(ProbeDescriptor pd: getDataSources().keySet()) {
			Long recorded = lastRecorded.get(pd);
			if (recorded == null || recorded < timeStamp) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	public void update(Observable o, Object arg) {
		if ((Object)o instanceof Probe) {
			if (arg instanceof BenchmarkProbeValue) {
				BenchmarkProbeValue value = (BenchmarkProbeValue)arg;
				record(value);
			}
		}
	}
}
