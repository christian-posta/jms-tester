package com.fusesource.forge.jmstest.tests.MB526;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.executor.BenchmarkProducerWrapper;
import com.fusesource.forge.jmstest.executor.ConsumerToProducerListener;
import com.fusesource.forge.jmstest.executor.AbstractTestNGSpringJMSTest;
import com.fusesource.forge.jmstest.executor.ProducerToConsumerListener;

@ContextConfiguration(locations={
		"classpath:com/fusesource/forge/jmstest/tests/MB526/broker-services.xml",
		"classpath:com/fusesource/forge/jmstest/tests/MB526/config-beans.xml",
		"classpath:com/fusesource/forge/jmstest/tests/MB526/messaging-beans.xml",
		"classpath:com/fusesource/forge/jmstest/tests/MB526/profiles.xml",
		"classpath:com/fusesource/forge/jmstest/tests/MB526/stats.xml"
})
		
public class MB526SimpleTest extends AbstractTestNGSpringJMSTest {

	@Test
    public void benchmark() {
		ConsumerToProducerListener cpl = (ConsumerToProducerListener)getBeanByClass(ConsumerToProducerListener.class);
		cpl.initialise(BenchmarkContext.getInstance().getTestrunConfig());
		
		ProducerToConsumerListener listener = (ProducerToConsumerListener)getBeanByClass(ProducerToConsumerListener.class);
		listener.initialize(BenchmarkContext.getInstance().getTestrunConfig());

		BenchmarkProducerWrapper bpw = (BenchmarkProducerWrapper)getBeanByClass(BenchmarkProducerWrapper.class);
		bpw.initialise(BenchmarkContext.getInstance().getTestrunConfig(), BenchmarkContext.getInstance().getProfile());
		
		bpw.benchmark();
    }
}
