package com.fusesource.forge.jmstest.tests.simple;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.executor.AbstractTestNGSpringJMSTest;


@ContextConfiguration(locations={
		"classpath:com/fusesource/forge/jmstest/tests/simple/broker-services.xml",
		"classpath:com/fusesource/forge/jmstest/tests/simple/test-beans.xml",
		"classpath:com/fusesource/forge/jmstest/tests/simple/profiles.xml"})
		
public class SimpleTest extends AbstractTestNGSpringJMSTest {

	@Test
    public void benchmark() {
		super.benchmark();
    }
}
