/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.executor.Releaseable;

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

    public ReleaseManager() {
    }

    public void initialise() {
        //Runtime.getRuntime().addShutdownHook(this);
        releaseables = new Vector<Releaseable>();
    }

    public void register(Releaseable releaseable) {
        releaseables.add(releaseable);
    }

    public void deregister(Releaseable releaseable) {
        releaseables.remove(releaseable);
    }

    @Override
    public void run() {
        log().info("ShutdownHook called, attempting to sweep up resources");
        
        List<Releaseable> toRelease = new ArrayList<Releaseable>();
        toRelease.addAll(releaseables);
        List<Releaseable> released = new ArrayList<Releaseable>();
        
        for (Releaseable releaseable : toRelease) {
            releaseable.release();
            released.add(releaseable);
        }
        
        for (Releaseable releaseable : released) {
            if (releaseables.contains(releaseable)) {
                releaseables.remove(releaseable);
            }
        }
        
        toRelease.clear();
        
        log().info("sweeping complete");
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
