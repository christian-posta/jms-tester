package com.fusesource.forge.jmstest.probe.sigar;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;

public class CpuStat extends AbstractSigarProbe {
	
	public CpuStat() {
		super();
	}

	public CpuStat(String name) {
		super(name);
	}

	@Override
	protected Number getValue() {
		
		try {
			CpuPerc cpu = getSigar().getCpuPerc();
			return cpu.getCombined() * 100;
		} catch (SigarException se) {
			se.printStackTrace();
		}
		return 0.0;
	}
}
