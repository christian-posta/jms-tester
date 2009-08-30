package com.fusesource.forge.jmstest.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.ReportStatsCommand;
import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;
import com.fusesource.forge.jmstest.probe.MaximizingProbe;
import com.fusesource.forge.jmstest.probe.MinimizingProbe;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public abstract class AbstractBenchmarkPostProcessor implements BenchmarkPostProcessor {

	private Map<String, ProbeDescriptor> distinctProbes = null;
	private File workDir = null;
	private Long valueCount = null;
	private MinimizingProbe startTime = null;
	private MaximizingProbe endTime = null;
	
	private Log log = null;
	
	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}
	
	public File getWorkDir() {
		return workDir;
	}
	
	private void gatherStats() {

		startTime = new MinimizingProbe();
		endTime = new MaximizingProbe();
		valueCount = new Long(0);
		distinctProbes = new TreeMap<String, ProbeDescriptor>();

		for(SampleIterator si = new SampleIterator(getWorkDir()); si.hasNext();) {
			BenchmarkProbeValue value = si.next();
			startTime.addValue(value.getTimestamp());
			endTime.addValue(value.getTimestamp());
			valueCount++;
			if (!distinctProbes.containsKey(value.getDescriptor().getName())) {
				distinctProbes.put(value.getDescriptor().getName(), value.getDescriptor());
			}
		}
		log().debug("Found " + valueCount + " BenchmarkProbes");
		log().debug("StartTime: " + startTime.getValue().longValue());
		log().debug("EndTime: " + endTime.getValue().longValue());
	}

	protected Map<String, ProbeDescriptor> getDistinctProbes() {
		if (distinctProbes == null) {
			gatherStats();
		}
		return distinctProbes;
	}
	
	protected long getValueCount() {
		if (valueCount == null) {
			gatherStats();
		}
		return valueCount;
	}

	protected long getStartTime() {
		if (startTime == null) {
			gatherStats();
		}
		return startTime.getValue().longValue();
	}
	
	protected long getEndTime() {
		if (endTime == null) {
			gatherStats();
		}
		return endTime.getValue().longValue();
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
	
	protected class SampleIterator implements Iterator<BenchmarkProbeValue> {

		private File workDir = null;
		String[] fileNames = null;
		private int current = 0;
		private Iterator<BenchmarkProbeValue> currentIterator = null; 
		
		public SampleIterator(File workDir) {
			
			this.workDir = workDir;
			
			fileNames = workDir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".raw");
				}
			});
		} 

		private void readFile() {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(workDir.getAbsoluteFile() + "/" + fileNames[current]);
				ois = new ObjectInputStream(fis);
				ReportStatsCommand stats = (ReportStatsCommand)ois.readObject();
				currentIterator = stats.getValues().iterator();
				current++;
			} catch (Exception e) {
				currentIterator = null;
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
						// TODO
					}
				}
			}
		}
		
		public boolean hasNext() {
			if (currentIterator == null && current < fileNames.length) {
				readFile();
			}
			
			if (currentIterator != null) {
				if (currentIterator.hasNext()) {
					return true;
				}
			} else {
				return false;
			}

			while (current < fileNames.length) {
				readFile();
				if (currentIterator != null && currentIterator.hasNext()) {
					return true;
				}
			}
			return false;
		}

		public BenchmarkProbeValue next() {
			if (hasNext()) {
				return currentIterator.next();
			} else {
				return null;
			}
		}

		public void remove() {
		}
	}
}
