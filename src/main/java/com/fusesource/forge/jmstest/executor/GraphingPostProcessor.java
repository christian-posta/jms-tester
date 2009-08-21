package com.fusesource.forge.jmstest.executor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.Archive;
import org.rrd4j.core.RrdDb;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

import com.fusesource.forge.jmstest.benchmark.command.ReportStatsCommand;
import com.fusesource.forge.jmstest.probe.MaximizingProbe;
import com.fusesource.forge.jmstest.probe.MinimizingProbe;
import com.fusesource.forge.jmstest.rrd.BenchmarkSample;
import com.fusesource.forge.jmstest.rrd.BenchmarkSampleRecorder;
import com.fusesource.forge.jmstest.rrd.BenchmarkSampleRecorderImpl;
import com.fusesource.forge.jmstest.rrd.Rrd4jSamplePersistenceAdapter;

public class GraphingPostProcessor implements BenchmarkPostProcessor {
	
	private transient Log log = null;
	
	public void processData(File workDir) {
		log().debug("Processing data from directory: " + workDir.getAbsolutePath());
		
		MinimizingProbe minTime = new MinimizingProbe();
		MaximizingProbe maxTime = new MaximizingProbe();
		
		Rrd4jSamplePersistenceAdapter rrd = new Rrd4jSamplePersistenceAdapter();
		rrd.setFileName(workDir.getAbsolutePath() + "/data.rrd");
		rrd.setStep(1);
		rrd.setAutoStart(false);
	
		Map<String, BenchmarkSampleRecorderImpl> recorders = new HashMap<String, BenchmarkSampleRecorderImpl>();
		Map<String, String> recorderNames = new HashMap<String, String>();
		
		for(SampleIterator si = new SampleIterator(workDir); si.hasNext();) {
			BenchmarkSample sample = si.next();
			
			for(String name: sample.getValues().keySet()) {
				if (!recorders.containsKey(name)) {
					BenchmarkSampleRecorderImpl bsr = new BenchmarkSampleRecorderImpl();
					String hash = "" + name.hashCode();
					recorderNames.put(hash, bsr.getName());
					bsr.setName(hash);
					bsr.setDsType(sample.getDsType());
					bsr.setAdapter(rrd);
					recorders.put(name, bsr);
				}
			}
			minTime.addValue(sample.getTimestamp());
			maxTime.addValue(sample.getTimestamp());
		}

		for(BenchmarkSampleRecorder recorder: recorders.values()) {
			rrd.addRecorder(recorder);
		}
		
		rrd.setArchiveLength(maxTime.getValue().intValue() - minTime.getValue().intValue() + 1);
		rrd.init(minTime.getValue().longValue());

		for(SampleIterator si = new SampleIterator(workDir); si.hasNext();) {
			BenchmarkSample sample = si.next();
			
			for(String name: sample.getValues().keySet()) {
				BenchmarkSampleRecorderImpl bsr = recorders.get(name);
				bsr.record(sample.getTimestamp(), sample.getValues().get(name));
			}
		}
		
		rrd.release();

		RrdDb db = rrd.getDatabase();
		try {
			for(int i=0; i<db.getArcCount(); i++) {
				Archive arch = db.getArchive(i);
					for(String dsName: db.getDsNames()) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
						
						StringBuffer title = new StringBuffer(recorderNames.get(dsName));
						title.append(" ");
						title.append(sdf.format(new Date(arch.getStartTime() * 1000)));
						title.append("-");
						title.append(sdf.format(new Date(arch.getEndTime() * 1000)));
						
						log().debug("Creating Graph: " + title);

						RrdGraphDef graphDef = new RrdGraphDef();
						
						graphDef.setTitle(title.toString());
						graphDef.setTimeSpan(arch.getStartTime(), arch.getEndTime());
						graphDef.datasource(dsName, rrd.getFileName(), dsName, ConsolFun.AVERAGE);
						graphDef.setStep(arch.getArcStep());
						graphDef.setWidth(800);
						graphDef.setHeight(600);
						graphDef.line(dsName, Color.BLACK, dsName);
						graphDef.setImageFormat("PNG");
						graphDef.setFilename(workDir.getAbsolutePath() + "/" + dsName + ".png");
						RrdGraph graph = new RrdGraph(graphDef);
						BufferedImage bi = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
						graph.render(bi.getGraphics());	
					}
			}
		} catch (Exception e) {
			log().error("Error while generating graphs for : " + rrd.getFileName(), e);
		}
		
		rrd.release();
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		} 
		return log;
	}
	
	private class SampleIterator implements Iterator<BenchmarkSample> {

		private File workDir = null;
		String[] fileNames = null;
		private int current = 0;
		private Iterator<BenchmarkSample> currentIterator = null; 
		
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
				currentIterator = stats.getSamples().iterator();
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

		public BenchmarkSample next() {
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
