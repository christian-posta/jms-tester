package com.fusesource.forge.jmstest.executor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.command.ShutdownCommand;
import com.fusesource.forge.jmstest.benchmark.command.SubmitBenchmarkCommand;

public class CommandLineClient extends AbstractBenchmarkExecutor {

	private Log log = null;
	private List<BenchmarkCommand> commands = null;

	private ApplicationContext getApplicationContext(String springConfigDirs) {
		
		ApplicationContext applicationContext = null;

		List<String> cfgLocations = new ArrayList<String>();
		StringTokenizer sTok = new StringTokenizer(springConfigDirs, ":");
		while(sTok.hasMoreTokens()) {
			String fName = sTok.nextToken();
			File f = new File(fName);
			if (f.exists()) {
				if (f.canRead()) {
					if (f.isDirectory()) {
						for(String fileName: f.list(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								File candidate = new File(dir, name);
								if (!candidate.isFile()) {
									return false;
								} else {
									return name.endsWith(".xml");
								}
							}
						})) {
							String absFileName = new File(fName, fileName).getAbsolutePath();
							log().debug("Found xml file: " + absFileName);
							cfgLocations.add("file://" + absFileName);
						}
					} else if (f.isFile()) {
						if (fName.endsWith(".xml")) {
							String absFileName = f.getAbsolutePath();
							log().debug("Found xml file: " + absFileName);
							cfgLocations.add("file://" + absFileName);
						}
					}
				}
			}
		}
		String[] configLocations = cfgLocations.toArray(new String[0]);
		try {
			applicationContext = new FileSystemXmlApplicationContext(configLocations);
		} catch (BeansException be) {
			log().error("Could not create Application Context.", be);
		}
		if (log().isDebugEnabled()) {
			for(String beanName: applicationContext.getBeanDefinitionNames()) {
				log().debug("Found bean: " + beanName);
			}
		}
		return applicationContext;
	}

	public void setCommand(String command) {
		if (command.equals("shutdown")) {
			commands.add(new ShutdownCommand());
		}
		
		if (command.startsWith("submit:")) {
			String configDirs = command.substring("submit:".length());
			ApplicationContext appContext = getApplicationContext(configDirs);
			String[] beanNames = appContext.getBeanNamesForType(BenchmarkConfig.class);
			for(String name: beanNames) {
				BenchmarkConfig cfg = (BenchmarkConfig)appContext.getBean(name);
				cfg.getSpringConfigurations();
				log().info("Submitting benchmark: " + cfg.getBenchmarkId());
				commands.add(new SubmitBenchmarkCommand(cfg));
			}
		}
	}
	
	private void run(String[] args) {
		commands = new ArrayList<BenchmarkCommand>();
		handleArguments(args);
		for(BenchmarkCommand cmd : commands) {
			getCmdTransport().sendCommand(cmd);
		}
		getCmdTransport().stop();
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}

	
	public static void main(String[] args) {
		new CommandLineClient().run(args);
	}
}
