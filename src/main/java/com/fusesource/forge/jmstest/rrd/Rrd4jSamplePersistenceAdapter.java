package com.fusesource.forge.jmstest.rrd;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public class Rrd4jSamplePersistenceAdapter extends ProbeAwarePeristenceAdapter {
	
	private long   step = 300L;
	private int    archiveLength = 100;

	private RrdDb database;
	
	public Rrd4jSamplePersistenceAdapter(List<ProbeDescriptor> descriptors) {
		super(descriptors);
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
	
	private void createRrdDatabase() {
		File dbFile = new File(getFileName());
		log().debug("Creating RRD database: " + dbFile.getAbsolutePath());
		
		RrdDef rrdDef = new RrdDef(dbFile.getAbsolutePath(), getStartTime(), 1);

		for(ProbeDescriptor pd : getDataSources().keySet()) {
			log().debug("Adding Data Source to RRD: " + pd + "," + getDataSources().get(pd));

			DsType type = null;
			
			switch(pd.getValueType()) {
			case COUNTER:
				type = DsType.COUNTER;
				break;
			default:
				type = DsType.GAUGE;
				break;
			}
			rrdDef.addDatasource(getDataSources().get(pd), type, 1, Double.NaN, Double.NaN);
		}
		
		rrdDef.setStartTime(getStartTime());
		rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 1, getArchiveLength());
		
		try {
			database = new RrdDb(rrdDef);
			database.close();
		} catch (IOException e) {
			log().error("Error creating RRD4J storage: ", e);
		}
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
		super.init();
		createRrdDatabase();
	}
	
	@Override
	protected void flushValues(List<BenchmarkProbeValue> values) {
		if (values.size() > 0) {
			try {
				long timeStamp = values.get(0).getTimestamp();
				Sample writeSample = getDatabase().createSample();
				writeSample.setTime(timeStamp);
				for(BenchmarkProbeValue v: values) {
					writeSample.setValue(getDataSources().get(v.getDescriptor()), v.getValue().doubleValue());
				}
				writeSample.update();
			} catch (Exception e) {
				log().error("Error flusging sample.", e);
			}
		}
	}
	
	public void release() {
		try {
			if (database != null) {
				database.close();
			}
		} catch (IOException e) {}
		super.release();
		log().info("Wriiten probe values to rrd database : " + getFileName());
	}
	
	private Log log() {
		return LogFactory.getLog(this.getClass());
	}
}
