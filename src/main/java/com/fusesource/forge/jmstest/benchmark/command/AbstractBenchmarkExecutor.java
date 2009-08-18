package com.fusesource.forge.jmstest.benchmark.command;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.executor.Releaseable;
import com.fusesource.forge.jmstest.executor.TerminatingThreadPoolExecutor;

public abstract class AbstractBenchmarkExecutor implements Runnable, Releaseable {

	private String connectionProviderName;
	private String destinationProviderName;
	
	private String destinationName = "topic:benchmark.command";
	private String springConfigDirs; 
	
	private JMSCommandTransport cmdTransport = new JMSCommandTransport(this);
	private BenchmarkCommandChainHandler handler = new BenchmarkCommandChainHandler();
	
	private CountDownLatch latch;
	private Log log = null;

	private ApplicationContext applicationContext = null;
	
	public AbstractBenchmarkExecutor() {
		createHandlerChain();
	}

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
					latch.countDown();
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
		getCmdTransport().sendMessage(command);
	}
	
	public final boolean handleCommand(BenchmarkCommand command) {
		log().debug("Executing Command: " + command);
		boolean result = getCommandHandler().handleCommand(command);
		if (!result) {
			log.warn("No handler found for commad: " + command);
		}
		return result;
	}
	
	protected void init() {
		log().info("Initializing Benchmarking framework ...");
		
		ReleaseManager.getInstance().register(this); 
		
		log().debug("Initializing the Spring Context");
		getApplicationContext();
		getCmdTransport().start();
	}
	
	public void run() {
		init();
		log().info("Running Benchmarking framework ...");
		latch = new CountDownLatch(1);
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

	public void stop() {
		latch.countDown();
	}
	
	public void release() {
		getCmdTransport().release();
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

	public Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
	
	public JMSCommandTransport getCmdTransport() {
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

	private ApplicationContext getApplicationContext() {
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
			applicationContext = new FileSystemXmlApplicationContext(configLocations);
		}
		return applicationContext;
	}
	
	protected static void start(AbstractBenchmarkExecutor instance, String[] args) {
		ExecutorService service = new TerminatingThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		instance.setSpringConfigLocations(args);
		service.submit(instance);
	}
}
