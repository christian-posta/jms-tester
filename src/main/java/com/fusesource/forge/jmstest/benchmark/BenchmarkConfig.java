package com.fusesource.forge.jmstest.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkProbeConfig;
import com.fusesource.forge.jmstest.config.SpringConfigHelper;

public class BenchmarkConfig implements Serializable {

	private static final long serialVersionUID = -4812138288497698831L;
	
	private String benchmarkId = "Benchmark-" + UUID.randomUUID().toString();
	private List<BenchmarkPartConfig> benchmarkParts = null;
	private List<String> springConfigurations = null;
	private String cfgBaseDirectory;
	private List<BenchmarkProbeConfig> probeConfigurations = null;
	
	private List<String> configLocations;
	private ApplicationContext applictionContext;
	
	private Log log = null;
	
	public String getBenchmarkId() {
		return benchmarkId;
	}
	
	public void setBenchmarkId(String benchMarkId) {
		this.benchmarkId = benchMarkId;
	}
	
	public List<BenchmarkPartConfig> getBenchmarkParts() {
		return benchmarkParts;
	}
	
	public void setBenchmarkParts(List<BenchmarkPartConfig> benchmarkParts) {
		this.benchmarkParts = benchmarkParts;
		if (benchmarkParts != null) {
			for (BenchmarkPartConfig partConfig: benchmarkParts) {
				partConfig.setParent(this);
			}
		}
	}
	
	public List<BenchmarkProbeConfig> getProbeConfigurations() {
		return probeConfigurations;
	}

	public void setProbeConfigurations(
		List<BenchmarkProbeConfig> probeConfigurations) {
		this.probeConfigurations = probeConfigurations;
	}

	public List<String> getSpringConfigurations() {
		if (springConfigurations == null) {
			log().debug("Trying to read Spring configurations for BenchmarkConfig ID: " + getBenchmarkId());
			SpringConfigHelper sch = new SpringConfigHelper();
			sch.setBaseDir(getCfgBaseDirectory());
			sch.setSpringConfigLocations(getConfigLocations());
			
			springConfigurations = new ArrayList<String>();
			
			for(String fileName: sch.getSpringConfigLocations()) {
				addSpringConfig(new File(fileName)); 
			}
			
			applictionContext = null;
		}
		return springConfigurations;
	}
	
	public void setSpringConfigurations(List<String> springConfigurations) {
		this.springConfigurations = springConfigurations;
	}
	
	public List<String> getConfigLocations() {
		return configLocations;
	}
	
	public void setConfigLocations(List<String> configLocations) {
		this.configLocations = configLocations;
	}
	
	public String getCfgBaseDirectory() {
		if (cfgBaseDirectory == null) {
			cfgBaseDirectory = System.getProperty("user.dir");
		}
		return cfgBaseDirectory;
	}

	public void setCfgBaseDirectory(String cfgBaseDirectory) {
		this.cfgBaseDirectory = cfgBaseDirectory;
	}
	
	private void addSpringConfig(File cfgFile) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(cfgFile);
			log().debug("Reading spring config file to memory: " + cfgFile.getAbsolutePath());
			springConfigurations.add(IOUtils.toString(fis));
			fis.close();
		} catch (Exception e) {
			log().error("Error reading spring config file: " + cfgFile.getAbsolutePath(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public ApplicationContext getApplicationContext() {
		if (applictionContext == null) {
			if (getSpringConfigurations().size() > 0) {
				File tmpDir = new File(System.getProperty("java.io.tmpdir"), getBenchmarkId());
				if (tmpDir.exists()) {
					if (tmpDir.isFile()) {
						FileUtils.deleteQuietly(tmpDir);
					} else if (tmpDir.isDirectory()) {
						try {
							FileUtils.deleteDirectory(tmpDir);
						} catch (IOException ioe) {
							log().error("Error deleting directory: " + tmpDir, ioe);
						}
					}
				}
				tmpDir.mkdirs();
				log().debug("Created directory: " + tmpDir);
				
				String[] tempFiles = new String[getSpringConfigurations().size()];
				
				int cfgNumber = 0;
				for(String config: getSpringConfigurations()) {
					File cfgFile = new File(tmpDir, "SpringConfig-" + (cfgNumber) + ".xml");
					tempFiles[cfgNumber++] = "file://" + cfgFile.getAbsolutePath();
					log().debug("Dumping : " + cfgFile.getAbsolutePath());
					try {
						OutputStream os = new FileOutputStream(cfgFile);
						IOUtils.write(config.getBytes(), os);
						os.close();
					} catch (Exception e) {
						log().error("Error Dumping: " + cfgFile.getAbsolutePath());
					}
				}
				log().debug("Creating ApplicationContext from temporary files.");
				applictionContext = new FileSystemXmlApplicationContext(tempFiles);
			} else {
				applictionContext = new FileSystemXmlApplicationContext();
			}
		}
		return applictionContext;
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
	
}
