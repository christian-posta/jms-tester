package com.fusesource.forge.jmstest.rrd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RRDController implements ApplicationContextAware, InitializingBean, DisposableBean {
	
	private String fileName = "jmstest.rrd"; 
	private long   step = 300L;
	private List<RRDRecorder> rrdRecorder;
	private ApplicationContext ac;

	private RrdDb database;
	
	public RRDController() {}

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
	
	public List<RRDRecorder> getRrdRecorder() {
		if (rrdRecorder != null) {
			return rrdRecorder;
		}
		return new ArrayList<RRDRecorder>();
	}

	public void record(RRDRecorder recorder, long timestamp, Number value) {
		try {
			Sample sample = database.createSample();
			sample.setTime(timestamp);
			sample.setValue(recorder.getName(), value.doubleValue());
			sample.update();
		} catch (IOException ioe) {
		    //TODO: handle this
		}
	}
	
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		File dbFile = new File(getFileName());
		
		if (!dbFile.getAbsoluteFile().getParentFile().exists()) {
			dbFile.getCanonicalFile().mkdirs();
		}
		
		RrdDef rrdDef = new RrdDef(getFileName(), System.currentTimeMillis() / 1000, getStep());
		
		Map<String, Object> recorders = ac.getBeansOfType(RRDRecorder.class);

		rrdRecorder = new ArrayList<RRDRecorder>();
		
		for(Object obj: recorders.values()) {
			RRDRecorder recorder = (RRDRecorder)obj;
			rrdRecorder.add(recorder);
			recorder.setController(this);
			rrdDef.addDatasource(recorder.getName(), recorder.getDsType(), getStep(), Double.NaN, Double.NaN);
		}

		rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 1, 1000);
		database = new RrdDb(rrdDef);
	}
	
	public void destroy() throws Exception {
		database.close();
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ac = applicationContext;
	}
}
