package com.fusesource.forge.jmstest.executor;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rrd4j.DsType;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfigurationException;
import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.config.TestRunConfig;
import com.fusesource.forge.jmstest.probe.AveragingProbe;
import com.fusesource.forge.jmstest.probe.CountingProbe;
import com.fusesource.forge.jmstest.probe.ProbeRunner;
import com.fusesource.forge.jmstest.rrd.RRDController;
import com.fusesource.forge.jmstest.rrd.RRDRecorder;

public class BenchmarkConsumer implements MessageListener, Releaseable  {
    private transient Log log;

    private JMSConnectionProvider connectionProvider;
    private JMSDestinationProvider destinationProvider;
    
    private Connection conn;
    private Session session;
    private MessageConsumer messageConsumer;
    private String clientId;
    
    private ProbeRunner probeRunner;
    private CountingProbe msgCounterProbe;
    private AveragingProbe latencyProbe;
    private AveragingProbe msgSizeProbe;
    private RRDController rrdController;

    public void setClientId(int clientId) {
        this.clientId = "bmClient-" + clientId;
    }
    
    public String getClientId() {
    	return this.clientId;
    }

    public JMSConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	public void setConnectionProvider(JMSConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	public JMSDestinationProvider getDestinationProvider() {
		return destinationProvider;
	}

	public void setDestinationProvider(JMSDestinationProvider destinationProvider) {
		this.destinationProvider = destinationProvider;
	}

	public ReleaseManager getReleaseManager() {
		return BenchmarkContext.getInstance().getReleaseManager();
	}

	public CountingProbe getMsgCounterProbe() {
		if (msgCounterProbe == null) {
			msgCounterProbe = new CountingProbe();
			msgCounterProbe.setName(getClientId() + "-Counter");
			RRDRecorder recorder = new RRDRecorder();
			recorder.setProbe(msgCounterProbe);
			recorder.setDsType(DsType.COUNTER);
			recorder.setController(rrdController);
			msgCounterProbe.setDataConsumer(recorder);
		}
		return msgCounterProbe;
	}

	public void setMsgCounterProbe(CountingProbe msgCounterProbe) {
		this.msgCounterProbe = msgCounterProbe;
	}

	public AveragingProbe getLatencyProbe() {
		if (latencyProbe == null) {
			latencyProbe = new AveragingProbe();
			latencyProbe.setName(getClientId() + "-Latency");
			RRDRecorder recorder = new RRDRecorder();
			recorder.setProbe(latencyProbe);
			recorder.setDsType(DsType.GAUGE);
			recorder.setController(rrdController);
			latencyProbe.setDataConsumer(recorder);
		}
		return latencyProbe;
	}

	public void setLatencyProbe(AveragingProbe latencyProbe) {
		this.latencyProbe = latencyProbe;
	}

	public AveragingProbe getMsgSizeProbe() {
		if (msgSizeProbe == null) {
			msgSizeProbe = new AveragingProbe();
			msgSizeProbe.setName(getClientId() + "-MsgSize");
			RRDRecorder recorder = new RRDRecorder();
			recorder.setProbe(msgSizeProbe);
			recorder.setDsType(DsType.GAUGE);
			recorder.setController(rrdController);
			msgSizeProbe.setDataConsumer(recorder);
		}
		return msgSizeProbe;
	}

	public void setMsgSizeProbe(AveragingProbe msgSizeProbe) {
		this.msgSizeProbe = msgSizeProbe;
	}

	public ProbeRunner getProbeRunner() {
		if (probeRunner == null) {
			probeRunner = BenchmarkContext.getInstance().getProbeRunner();
		}
		return probeRunner;
	}

	public void setProbeRunner(ProbeRunner probeRunner) {
		this.probeRunner = probeRunner;
	}

	//TODO: simulate slow subscriber
    public void initialise(TestRunConfig testRunConfig, int clientId, RRDController rrdController) {
        this.setClientId(clientId);
        this.rrdController = rrdController;
        getReleaseManager().register(this);
        
        if (getProbeRunner() != null) {
        	getProbeRunner().addProbe(getMsgCounterProbe());
        	getProbeRunner().addProbe(getLatencyProbe());
        	getProbeRunner().addProbe(getMsgSizeProbe());
        }
        
        try {
            conn = getConnectionProvider().getConnection();
            session = conn.createSession(false, testRunConfig.getAcknowledgeMode().getCode());
              // TODO: Handle Durable subscribers
            Destination dest = getDestinationProvider().getDestination(
            		session, testRunConfig.getTestDestinationName());
            messageConsumer = session.createConsumer(dest);
            messageConsumer.setMessageListener(this);
            conn.start();
        } catch (Exception e) {
            throw new BenchmarkConfigurationException("Unable to initialise JMS connection", e);
        }
    }

    public void onMessage(Message message) {
        //TODO: simulate slow subscriber with wait(n)
        try {
            long now = System.currentTimeMillis();
            long latency = now - message.getLongProperty("SendTime");
            if (getMsgCounterProbe() != null) {
            	getMsgCounterProbe().increment();
            }
            if (getLatencyProbe() != null) {
            	getLatencyProbe().addValue(new Double(latency));
            }
            if (getMsgSizeProbe() != null) {
            	getMsgSizeProbe().addValue(new Double(getMessageSize(message)));
            }
        } catch (JMSException e) {
            log().warn("SendTime not available in message properties", e);
        }
    }

    private int getMessageSize(Message message) {
        int messageSize = 0;
        try {
            if (message instanceof BytesMessage) {
                messageSize = (int) ((BytesMessage) message).getBodyLength();
            } else if (message instanceof TextMessage) {
                messageSize = ((TextMessage) message).getText().length();
            }
        } catch (JMSException e) {
            log().warn("Failed to obtain message size by reading message body", e);
        }
        return messageSize;
    }

    public void release() {
        log().trace(">>> BenchmarkConsumer#release");
        getReleaseManager().deregister(this);

        try {
            if (session != null) {
                session.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [session]", e);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [connection]", e);
        }
        log().trace("<<< BenchmarkConsumer#release");
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
