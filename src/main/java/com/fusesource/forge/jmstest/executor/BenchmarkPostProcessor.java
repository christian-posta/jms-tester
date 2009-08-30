package com.fusesource.forge.jmstest.executor;

import java.io.File;

public interface BenchmarkPostProcessor {

	public void setWorkDir(File workDir);
	public void processData();
}
