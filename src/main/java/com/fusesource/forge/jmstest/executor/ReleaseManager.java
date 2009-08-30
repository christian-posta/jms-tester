/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.executor;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a utility class where resources with cleanup requirements can be registered. 
 * An instance of this class needs to be registered as a shutdown hook within the JVM. Each 
 * registered object must implement the <code>Releasable</code> interface. Once the 
 * shutdown hook is triggered, the <code>release()</code> method of all releasables 
 * will be executed.
 * 
 * @author andreasgies
 * @see Releaseable
 */
public class ReleaseManager extends Thread {

    private transient Log log;
    private Vector<Releaseable> releaseables;
    static private ReleaseManager instance = null;
 
    synchronized static public ReleaseManager getInstance() {
    	if (instance == null) {
    		instance = new ReleaseManager();
    		Runtime.getRuntime().addShutdownHook(instance);
    	}
    	return instance;
    }
    
    private ReleaseManager() {
        releaseables = new Vector<Releaseable>();
    }

    public void register(Releaseable releaseable) {
    	synchronized (releaseables) {
            releaseables.add(releaseable);
		}
    }

    public void deregister(Releaseable releaseable) {
    	synchronized (releaseable) {
            releaseables.remove(releaseable);
		}
    }

    @Override
    public void run() {
        log().info("ShutdownHook called, attempting to sweep up resources");

        synchronized (releaseables) {
        	while (releaseables != null && releaseables.size() > 0) {
        		Releaseable r = releaseables.remove(0);
        		r.release();
        	}
		}
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
    
    static {
    	ReleaseManager.getInstance();
    }
}
