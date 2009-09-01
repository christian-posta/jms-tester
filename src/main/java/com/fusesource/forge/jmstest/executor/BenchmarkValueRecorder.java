package com.fusesource.forge.jmstest.executor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.EndBenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.PrepareBenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.ReportStatsCommand;
import com.fusesource.forge.jmstest.benchmark.command.handler.DefaultCommandHandler;

public class BenchmarkValueRecorder extends AbstractBenchmarkExecutionContainer {

	private String workDirectory = null; 
	private Log log = null;
	
	public String getWorkDirectory() {
		if (workDirectory == null) {
			workDirectory = System.getProperty("user.dir");
		}
		return workDirectory;
	}

	public void setWorkDirectory(String workDirectory) {
		this.workDirectory = workDirectory;
	}

	private List<BenchmarkPostProcessor> getPostProcessors() {
		List<BenchmarkPostProcessor> postProcessors = new ArrayList<BenchmarkPostProcessor>();
		
		for(String beanName: getApplicationContext().getBeanNamesForType(BenchmarkPostProcessor.class)) {
			BenchmarkPostProcessor bpp = (BenchmarkPostProcessor)getApplicationContext().getBean(beanName);
			postProcessors.add(bpp);
		}
		return postProcessors;
	}
	
	@Override
	protected void createHandlerChain() {
		super.createHandlerChain();
		
		getConnector().addHandler(new DefaultCommandHandler() {
			public boolean handleCommand(BenchmarkCommand command) {
				switch (command.getCommandType()) {
					case CommandTypes.PREPARE_BENCHMARK:
						PrepareBenchmarkCommand prepCmd = (PrepareBenchmarkCommand)command;
						File benchmarkDir = getBenchmarkWorkDirectory(prepCmd.getBenchmarkConfig().getBenchmarkId());
						try {
							FileUtils.deleteDirectory(benchmarkDir);
							benchmarkDir.mkdirs();
						} catch (IOException e) {
							log().error("Error creating directory : " + benchmarkDir.getAbsolutePath(), e);
							e.printStackTrace();
						}
						return true;
					case CommandTypes.REPORT_STATS:
						ReportStatsCommand stats = (ReportStatsCommand)command;
						recordStats(stats);
						return true;
					case CommandTypes.END_BENCHMARK:
						EndBenchmarkCommand endCommand = (EndBenchmarkCommand)command;
						final String benchmarkId = endCommand.getBenchmarkId();
						final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
						executor.schedule(new Runnable() {
							public void run() {
								for(BenchmarkPostProcessor processor : getPostProcessors()) {
									processor.resetStatistics();
									processor.setWorkDir(getBenchmarkWorkDirectory(benchmarkId));
									processor.processData();
								}
								executor.shutdown();	
							}
						}, 5, TimeUnit.SECONDS);
						return true;
					default:
						return false;
				}
			}
		});
	}

	private boolean checkDir(File dir) {
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				log().error(dir.getAbsolutePath() + " is not a directory.");
				return false;
			}
			if (!dir.canWrite()) {
				log().error(dir.getAbsolutePath() + " is not writable.");
				return false;
			}
		} else {
			dir.mkdirs();
		}
		return true;
	}
	
	private File getBenchmarkWorkDirectory(String benchmarkId) {
		
		File workDir = new File(getWorkDirectory());
		File result = new File(workDir, benchmarkId);
		checkDir(result);
		return result;
	}

	synchronized private void recordStats(ReportStatsCommand stats) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		OutputStream os = null;
		ObjectOutputStream oos = null;
		
		try {
			final String dateString = sdf.format(new Date());
			File targetDir = getBenchmarkWorkDirectory(stats.getClientId().getBenchmarkId());
			
			String fileNames[] = targetDir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith(dateString);
				}
			});
			File rawData = new File(getBenchmarkWorkDirectory(stats.getClientId().getBenchmarkId()), sdf.format(new Date()) + "-" + fileNames.length + ".raw");
			log().debug("Writing benchmark raw data to : " + rawData.getAbsolutePath());
			os = new FileOutputStream(rawData, false);
			oos = new ObjectOutputStream(os);
			oos.writeObject(stats);
		} catch (Exception e) {
			log().debug("Error Writing benchmark raw data.");
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
