package com.fusesource.forge.jmstest.tests.MB526;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkController;
import com.fusesource.forge.jmstest.executor.AbstractTestNGSpringJMSTest;

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
		
		String springDirs = "src/main/resources/com/fusesource/forge/jmstest/tests/MB526";
		
		BenchmarkController controller = new BenchmarkController();
		controller.setSpringConfigDirs(springDirs);
		
		
		super.benchmark();
		createGraphs();
    }
}
