package com.fusesource.forge.jmstest.benchmark.command;

public interface CommandTypes {
	
	byte NULL = 0;

	byte SUBMIT_BENCHMARK = 10;
	byte PREPARE_BENCHMARK = SUBMIT_BENCHMARK + 1;
	byte START_BENCHMARK = SUBMIT_BENCHMARK + 2;
	byte PRODUCER_FINISHED = SUBMIT_BENCHMARK + 3;
	byte END_BENCHMARK = SUBMIT_BENCHMARK + 4;
	byte PREPARE_RESPONSE = SUBMIT_BENCHMARK + 5;
	
	byte REPORT_STATS = 20;
	
	byte GET_CLIENT_INFO = 30;
	byte CLIENT_INFO = GET_CLIENT_INFO + 1;

	byte SHUTDOWN = Byte.MAX_VALUE;
}
