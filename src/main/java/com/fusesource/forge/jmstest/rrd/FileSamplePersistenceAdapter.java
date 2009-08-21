package com.fusesource.forge.jmstest.rrd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.ClientId;

public class FileSamplePersistenceAdapter extends CachingSamplePersistenceAdapter {
	
	private String fileName = "jmstest.raw"; 
	private OutputStream os = null;
	private ObjectOutputStream oos = null;

	public FileSamplePersistenceAdapter(ClientId clientId) {
		super(clientId);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	protected void startFlush() {
		super.startFlush();
		try {
			os = new FileOutputStream(getFileName(), true);
			oos = new ObjectOutputStream(os);
		} catch (IOException ioe) {
			log().error("Could not open file to write: " + getFileName(), ioe);
			os =null;
			oos = null;
		}
	}
	
	@Override
	protected void finishFlush() {
		try {
			oos.close();
			os.close();
		} catch (IOException ioe) {
			log().warn("Could not close file after write: " + getFileName(), ioe);
		} finally {
			oos  = null;
			os = null;
		}
		super.finishFlush();
	}

	@Override
	public void init() {
		super.init();
		
		File sampleFile = new File(getFileName());
		
		if (!sampleFile.getAbsoluteFile().getParentFile().exists()) {
			sampleFile.getParentFile().mkdirs();
		}
		
		if(sampleFile.exists()) {
			sampleFile.delete();
		}
	}
	
	@Override
	protected void flushSample(BenchmarkSample sample) {
		
		if (oos != null) {
			try {
				oos.writeObject(sample);
			} catch (IOException e) {
				log().error("Error writing sample to file: " + sample.toString(), e);
			}
		}
	}
	
	private Log log() {
		return LogFactory.getLog(this.getClass());
	}
}
