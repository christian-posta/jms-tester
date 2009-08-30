package com.fusesource.forge.jmstest.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkProbeConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.probe.AbstractProbe;
import com.fusesource.forge.jmstest.probe.Probe;

public class BenchmarkProbeWrapper extends AbstractBenchmarkClient {
	
	private ClientId clientId = null;
	
	private Log log = null;
	
	public BenchmarkProbeWrapper(BenchmarkClient container, BenchmarkConfig config) {
		super(container, config);
	}


	public ClientType getClientType() {
		return ClientType.PROBER;
	}
	
	public ClientId getClientId() {
		
		if (clientId == null) {
			clientId = new ClientId(
				getClientType(), getContainer().getClientInfo().getClientName(), getConfig().getBenchmarkId(), ""
			);
		}
		return clientId;
	}
	
	@Override
	public boolean prepare() {
		for(BenchmarkProbeConfig probeConfig : getConfig().getProbeConfigurations()) {
			if (getContainer().matchesClient(probeConfig.getClientNames())) {
				
				String[] probeNames = null;
				
				if (probeConfig.getProbeNames().equalsIgnoreCase("all")) {
					probeNames = getApplicationContext().getBeanNamesForType(Probe.class);
				} else {
					List<String> names = new ArrayList<String>();
					
					StringTokenizer sTok = new StringTokenizer(probeConfig.getProbeNames(), ",");
					while(sTok.hasMoreTokens()) {
						names.add(sTok.nextToken());
					}
					
					probeNames = names.toArray(new String[0]);
				}
				
				for(String probeName : probeNames) {
					try {
						Probe p = (Probe)getBean(
							new String[] { probeName }, null
						);
						((AbstractProbe)p).setName(getClientId().toString() + p.getName());
						((AbstractProbe)p).addObserver(getSamplePersistenceAdapter());
						getProbeRunner().addProbe(p);
					} catch (Exception e) {
						log().error("Could not create probe: " + probeName, e);
					}
				}
			}
		}
		
		if (getProbeRunner().getProbes().size() == 0) {
			return false;
		}
		
		return super.prepare();
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}

}
