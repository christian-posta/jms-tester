package com.fusesource.forge.jmstest.probe.sigar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.SigarException;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue.ValueType;

public class IOStat extends AbstractSigarProbe {

	private IOStatType type = IOStatType.DISK_READ_BYTES;
	private Log log = null;
	
	public IOStat() {
		super();
	}

	public IOStat(String name) {
		super(name);
	}

	public void setIOStatType(IOStatType type) {
		this.type =  type;
	}
	
	public IOStatType getType() {
		return type;
	}
	
	@Override
	public ValueType getValueType() {
		return ValueType.COUNTER;
	}
	
	@Override
	protected Number getValue() {
		
		long result = 0;
		
		try {
			for(FileSystem fs: getSigar().getFileSystemList()) {
				FileSystemUsage fsu = new FileSystemUsage();
				try {
					if (!"none".equalsIgnoreCase(fs.getTypeName())) {
						fsu.gather(getSigar(), fs.getDevName());
						switch(getType()) {
						case DISK_READ_BYTES:
							result += fsu.getDiskReadBytes();
							break;
						case DISK_WRITE_BYTES:
							result += fsu.getDiskWriteBytes();
							break;
						case DISK_READS:
							result += fsu.getDiskReads();
							break;
						case DISK_WRITES:
							result += fsu.getDiskWrites();
							break;
						default:
							// do nothing
						}
					}
				} catch (SigarException se) { // swallow }
				}
			}
		} catch (SigarException e) {
			log().error("Unable to retrieve file System information.", e);
		}
		return result;
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
	
	public enum IOStatType {
		DISK_READ_BYTES, DISK_WRITE_BYTES, DISK_READS, DISK_WRITES
	}
}
