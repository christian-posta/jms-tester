package com.fusesource.forge.jmstest.probe.sigar;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;

public class CpuStat extends AbstractSigarProbe {
	
	@Override
	protected Number getValue() {
		
		try {
			CpuPerc cpu = getSigar().getCpuPerc();
			return cpu.getCombined();
		} catch (SigarException se) {
			se.printStackTrace();
		}
		return 0.0;
	}
}
