package com.fusesource.forge.jmstest.probe.sigar;

import org.hyperic.sigar.Sigar;

import com.fusesource.forge.jmstest.probe.AbstractProbe;

public abstract class AbstractSigarProbe extends AbstractProbe {

	private Sigar sigar = null;
	
	public AbstractSigarProbe() {
		super();
	}

	public AbstractSigarProbe(String name) {
		super(name);
	}

	public Sigar getSigar() {
		if (sigar == null) {
			sigar = new Sigar();
		}
		return sigar;
	}
}
