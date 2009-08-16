package com.fusesource.forge.jmstest.tests.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.fusesource.forge.jmstest.tests.MB526.MB526SimpleTest;

public class AMQTest {

	protected String testClassName = MB526SimpleTest.class.getName();
	
	@Test
	public void runTest() {
		XmlSuite suite = new XmlSuite();
		suite.setName("AMQTestSuite");

		XmlTest test = new XmlTest(suite);
		test.setName("AMQTest");
		List<XmlClass> classes = new ArrayList<XmlClass>();
		classes.add(new XmlClass(testClassName));
		test.setXmlClasses(classes);
		
		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(suite);
		TestNG tng = new TestNG();
		tng.setXmlSuites(suites);
		tng.run(); 
	}
}
