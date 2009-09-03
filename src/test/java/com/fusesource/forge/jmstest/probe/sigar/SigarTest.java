package com.fusesource.forge.jmstest.probe.sigar;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.probe.sigar.IOStat.IOStatType;

public class SigarTest {
	
	@BeforeTest
	public void addSigarLibs() {
		String libPath = System.getProperty("java.library.path") + File.pathSeparator + "src/main/lib";
		System.setProperty("java.library.path", libPath);
	}
	@Test
	public void testCpuStatProbe() {
		CpuStat cs = new CpuStat("CPU");
		Number n = cs.getValue();
		Assert.assertTrue(n.doubleValue() >=0.0 && n.doubleValue() <= 100.0);
	}

	@Test
	public void testIOStatProbe() {
		IOStat is = new IOStat("IOStat");
		is.setType(IOStatType.DISK_READS);
		Number n = is.getValue();
		System.out.println(is.getType() + ":" + n);
	}

}
