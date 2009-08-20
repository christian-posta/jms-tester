package com.fusesource.forge.jmstest.executor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommandChainHandler;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommandHandler;
import com.fusesource.forge.jmstest.benchmark.command.CommandTransport;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.DefaultCommandHandler;
import com.fusesource.forge.jmstest.benchmark.command.JMSCommandTransport;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;

public abstract class AbstractBenchmarkExecutor implements Runnable, Releaseable {

	private String connectionProviderName;
	private String destinationProviderName;
	private boolean autoTerminate = true;
	
	private String destinationName = "topic:benchmark.command";
	private String springConfigDirs; 
	
	private CommandTransport cmdTransport = null;
	
	private BenchmarkCommandChainHandler handler = new BenchmarkCommandChainHandler();
	
	private ExecutorService executor = null;
	private Boolean started = false;
	
	private CountDownLatch latch;
	private Log log = null;

	private ApplicationContext applicationContext = null;
	
	protected void setSpringConfigLocations(String[] args) {
		if (args.length == 0) {
			log().warn("No Spring Config locations given. Using current directory only");
			setSpringConfigDirs(System.getProperty("user.dir"));
		} else {
			setSpringConfigDirs(args[0]);
			if (args.length > 1) {
				log().warn("Ignoring additional Command line arguments");
			}
		}
	}
	
	protected BenchmarkCommandChainHandler getHandler() {
		return handler;
	}
	
	protected  void createHandlerChain() {
		getHandler().addHandler(new DefaultCommandHandler() {
			public boolean handleCommand(BenchmarkCommand command) {
				if (command.getCommandType() == CommandTypes.SHUTDOWN) {
					stop();
					return true;
				}
				return false;
			}
		});
	}
	
	private BenchmarkCommandHandler getCommandHandler() {
		return handler;
	}
	
	public final void sendCommand(BenchmarkCommand command) {
		getCmdTransport().sendCommand(command);
	}
	
	public final boolean handleCommand(BenchmarkCommand command) {
		log().debug("Executing Command: " + command);
		boolean result = getCommandHandler().handleCommand(command);
		if (!result) {
			log.warn("No handler found for commad: " + command);
		}
		return result;
	}
	
	public void start(String[] args) {
		synchronized (started) {
			if (!started) {
				if (isAutoTerminate()) {
					executor = new TerminatingThreadPoolExecutor("BenchmarkExecutor", 1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
				} else  {
					executor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			  	}
				setSpringConfigLocations(args);
				executor.submit(this);
				started = true;
			}
		}
	}

	public void stop() {
		synchronized (started) {
			if (started) {
				if (!isAutoTerminate()) {
					if (executor != null) {
						executor.shutdown();
					}
				}
			}
			started = false;
		}
		latch.countDown();
	}
	
	protected void init() {
		log().info("Initializing Benchmarking framework ...");
		ReleaseManager.getInstance().register(this); 
		
		cmdTransport = new JMSCommandTransport();
		((JMSCommandTransport)cmdTransport).setJmsConnectionProvider(getJmsConnectionProvider());
		((JMSCommandTransport)cmdTransport).setJmsDestinationProvider(getJmsDestinationProvider());
		((JMSCommandTransport)cmdTransport).setDestinationName(getDestinationName());
		cmdTransport.setHandler(getHandler());
		cmdTransport.start();

		createHandlerChain();
		log().debug("Initializing the Spring Context");
		getApplicationContext();
	}
	
	protected void execute() {
	}
	
	public void run() {
		init();
		log().info("Running Benchmarking framework ...");
		latch = new CountDownLatch(1);
		execute();

		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		release();
		ReleaseManager.getInstance().deregister(this);
		log().info("Done Benchmarking framework");
	}

	public void release() {
		getCmdTransport().stop();
	}
	
	public JMSConnectionProvider getJmsConnectionProvider() {
		return (JMSConnectionProvider)getApplicationContext().getBean(getConnectionProviderName());
	}

	public JMSDestinationProvider getJmsDestinationProvider() {
		return (JMSDestinationProvider)getApplicationContext().getBean(getDestinationProviderName());
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationname(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getSpringConfigDirs() {
		return springConfigDirs != null ? springConfigDirs : "";
	}

	public void setSpringConfigDirs(String springConfigDirs) {
		this.springConfigDirs = springConfigDirs;
	}

	public boolean isAutoTerminate() {
		return autoTerminate;
	}

	public void setAutoTerminate(boolean autoTerminate) {
		this.autoTerminate = autoTerminate;
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
	
	public CommandTransport getCmdTransport() {
		return cmdTransport;
	}

	public String getConnectionProviderName() {
		if (connectionProviderName == null) {
			String[] beanNames = getApplicationContext().getBeanNamesForType(JMSConnectionProvider.class);
			if (beanNames.length > 0) {
				connectionProviderName = beanNames[0];
				log.info("Using " + connectionProviderName + " as Connection provider bean.");
			}
		}
		return connectionProviderName;
	}

	public void setConnectionProviderName(String connectionProviderName) {
		this.connectionProviderName = connectionProviderName;
	}

	public String getDestinationProviderName() {
		if (destinationProviderName == null) {
			String[] beanNames = getApplicationContext().getBeanNamesForType(JMSDestinationProvider.class);
			if (beanNames.length > 0) {
				destinationProviderName = beanNames[0];
				log.info("Using " + destinationProviderName + " as Destination provider bean.");
			}
		}
		return destinationProviderName;
	}

	public void setDestinationProviderName(String destinationProviderName) {
		this.destinationProviderName = destinationProviderName;
	}

	protected ApplicationContext getApplicationContext() {
		if (applicationContext == null) {
			List<String> cfgLocations = new ArrayList<String>();
			StringTokenizer sTok = new StringTokenizer(getSpringConfigDirs(), ":");
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
				log.error("Could not create Application Context.", be);
			}
			if (log().isDebugEnabled()) {
				for(String beanName: applicationContext.getBeanDefinitionNames()) {
					log().debug("Found bean: " + beanName);
				}
			}
		}
		return applicationContext;
	}
	
}
