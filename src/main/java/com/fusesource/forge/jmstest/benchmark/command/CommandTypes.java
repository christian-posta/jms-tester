package com.fusesource.forge.jmstest.benchmark.command;

public interface CommandTypes {
	
	byte NULL = 0;

	byte PREPARE_BENCHMARK = 1;
	byte START_BENCHMARK = 2;
	byte END_BENCHMARK = 3;
	
	byte REPORT_STATS = 4;
	byte PREPARE_RESPONSE = 5;
	
	byte CLIENT_INFO = Byte.MAX_VALUE - 2;
	byte GET_CLIENT_INFO = Byte.MAX_VALUE - 1;
	byte SHUTDOWN = Byte.MAX_VALUE;
}
