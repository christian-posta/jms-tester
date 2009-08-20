package com.fusesource.forge.jmstest.benchmark.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BenchmarkCommandChainHandler implements BenchmarkCommandHandler {

	private Log log = null;
	private List<BenchmarkCommandHandler> handlerList = new ArrayList<BenchmarkCommandHandler>();
	
	public BenchmarkCommandChainHandler() {
		handlerList = new ArrayList<BenchmarkCommandHandler>();
	}
	
	public boolean handleCommand(BenchmarkCommand command) {
		
		log().debug("Handling Command: " + command);
		
		if (handlerList.size() == 0) {
			return false;
		}
		BenchmarkCommandHandler current = handlerList.get(0);
		
		boolean handled = false;
		while((current != null) && !handled) {
			handled = current.handleCommand(command);
			current = current.next();
		}

		if (!handled) {
			log().warn("Not handle Command: " + command);
		}
		
		return handled;
	}

	public BenchmarkCommandHandler next() {
		return null;
	}

	public void setNext(BenchmarkCommandHandler next) {
		//do noting
	}
	
	public void addHandler(BenchmarkCommandHandler handler) {
		if (handlerList.size() > 0) {
			BenchmarkCommandHandler last = handlerList.get(handlerList.size()-1);
			last.setNext(handler);
		}
		handlerList.add(handler);
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
