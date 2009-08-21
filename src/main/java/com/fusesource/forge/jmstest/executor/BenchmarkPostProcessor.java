package com.fusesource.forge.jmstest.executor;

import java.io.File;

public interface BenchmarkPostProcessor {

	public void processData(File workDir);
}
