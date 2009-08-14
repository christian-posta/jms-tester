package com.fusesource.forge.jmstest.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.benchmark.ReleaseManager;

public class LimitedTimeScheduledExecutor implements Runnable, Releaseable {

	private String name;
	
	private long duration = 300;
	private long interval = 5;
	private Runnable task;
	
	private ScheduledThreadPoolExecutor executor;
	private Thread controlThread;
	
	private boolean isRunning = false;
	
	private Object lock = new Object();
	CountDownLatch latch;
	List<CountDownLatch> waiting;

	public LimitedTimeScheduledExecutor() {
		waiting = new ArrayList<CountDownLatch>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		if (name == null) {
			name = this.getClass().getName() + "-" + UUID.randomUUID();
		}
		return name;
	}
	
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void setTask(Runnable task) {
		this.task = task;
	}
	
	public Runnable getTask() {
		return task;
	}
	
	public ReleaseManager getReleaseManager() {
		return BenchmarkContext.getInstance().getReleaseManager();
	}
	
	public void start() {
		synchronized (lock) {
			if (isRunning) {
				return;
			}
			controlThread = new Thread(this, getName());
			controlThread.start();
			isRunning = true;
		}
	}
	
	public void waitUntilFinished() {
		synchronized (lock) {
			if (!isRunning) {
				return;
			}
		}
		CountDownLatch latch = new CountDownLatch(1);
		synchronized (waiting) {
			waiting.add(latch);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
	}
	
	public void run() {
	  	log().info(getName() + " starting");
	  	
	  	getReleaseManager().register(this);
	  	
	  	executor = new ScheduledThreadPoolExecutor(1);
	  	executor.scheduleAtFixedRate(getTask(), 0, getInterval(), TimeUnit.SECONDS);
	  	
	  	latch = new CountDownLatch(1);
	  	
	  	new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {
			
			public void run() {
				try {
					release();
				} catch (Exception e) {}
			}
		}, duration, TimeUnit.SECONDS);
	  	
	  	try {
	  	  latch.await();
	  	} catch (InterruptedException ie) {
	  		try {
	  			release();
	  		} catch (Exception e) {}
	  	}
		isRunning = false;
	  	log().info("ProbeRunner finished");
	}
	
	public void release() {
		synchronized (lock) {
			if (!isRunning) {
				return;
			}
			if (executor != null) {
				log().debug("Closing " + getName());
				executor.shutdown();
				executor = null;
			}
			
			synchronized (waiting) {
				while(waiting.size() > 0) {
					CountDownLatch l = waiting.remove(0);
					l.countDown();
				}
			}
			latch.countDown();
			getReleaseManager().deregister(this);
		}
	}
	
	private Log log() {
		return LogFactory.getLog(this.getClass());
	}
}
