package com.fusesource.forge.jmstest.tests.simple;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.config.TestRunConfig;
import com.fusesource.forge.jmstest.executor.BenchmarkProducerWrapper;
import com.fusesource.forge.jmstest.executor.ConsumerToProducerListener;
import com.fusesource.forge.jmstest.executor.AbstractTestNGSpringJMSTest;
import com.fusesource.forge.jmstest.executor.ProducerToConsumerListener;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;


@ContextConfiguration(locations={
		"classpath:com/fusesource/forge/jmstest/tests/simple/broker-services.xml",
		"classpath:com/fusesource/forge/jmstest/tests/simple/test-beans.xml",
		"classpath:com/fusesource/forge/jmstest/tests/simple/profiles.xml"})
		
public class SimpleTest extends AbstractTestNGSpringJMSTest {

	@Test
    public void benchmark() {
		TestRunConfig testRunConfig = (TestRunConfig)getBeanByClass(TestRunConfig.class);
		
		ConsumerToProducerListener cpl = (ConsumerToProducerListener)getBeanByClass(ConsumerToProducerListener.class);
		cpl.initialise(testRunConfig);
		
		ProducerToConsumerListener listener = (ProducerToConsumerListener)getBeanByClass(ProducerToConsumerListener.class);
		listener.initialize(testRunConfig);
		
		BenchmarkProducerWrapper bpw = (BenchmarkProducerWrapper)getBeanByClass(BenchmarkProducerWrapper.class);
		BenchmarkIteration profile = (BenchmarkIteration)applicationContext.getBean("testProfile");
		bpw.initialise(testRunConfig, profile);
		
		bpw.benchmark();
    }
}
