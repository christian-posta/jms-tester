package com.fusesource.forge.jmstest.probe.sigar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.sigar.SigarException;

public class MemStat extends AbstractSigarProbe {

	private MemStatType type = MemStatType.MEMORY_FREE;
	private Log log = null;
	
	public MemStat() {
		super();
	}

	public MemStat(String name) {
		super(name);
	}

	public void setIOStatType(MemStatType type) {
		this.type =  type;
	}
	
	public MemStatType getType() {
		return type;
	}
	
	@Override
	protected Number getValue() {
		
		try {
			switch(getType()) {
				case MEMORY_FREE:
					return getSigar().getMem().getFree();
				case MEMORY_TOTAL:
					return getSigar().getMem().getTotal();
				case SWAP_FREE:
					return getSigar().getSwap().getFree();
				case SWAP_TOTAL:
					return getSigar().getSwap().getTotal();
				case SWAP_PAGE_IN:
					return getSigar().getSwap().getPageIn();
				case SWAP_PAGE_OUT:
					return getSigar().getSwap().getPageOut();
				default:
					return 0;
			}
		} catch (SigarException e) {
			log().error("Unable to retrieve file System information.", e);
		}
		
		return 0;
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
	
	public enum MemStatType {
		MEMORY_TOTAL, MEMORY_FREE, SWAP_TOTAL, SWAP_FREE, SWAP_PAGE_IN, SWAP_PAGE_OUT
	}
}
