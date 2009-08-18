package com.fusesource.forge.jmstest.benchmark.command;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkCommandChainHandler implements BenchmarkCommandHandler {

	List<BenchmarkCommandHandler> handlerList = new ArrayList<BenchmarkCommandHandler>();
	
	public BenchmarkCommandChainHandler() {
		handlerList = new ArrayList<BenchmarkCommandHandler>();
	}
	
	public boolean handleCommand(BenchmarkCommand command) {
		if (handlerList.size() == 0) {
			return false;
		}
		BenchmarkCommandHandler current = handlerList.get(0);
		
		boolean handled = false;
		while((current != null) && !handled) {
			handled = current.handleCommand(command);
			current = current.next();
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
}
