package com.fusesource.forge.jmstest.benchmark.results;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.config.MeasureTimeUnit;

/**
 * @author  andreasgies
 */
public class ProducerMetricCollector {

    /**
	 * @uml.property  name="messageCount"
	 */
    private AtomicLong messageCount;
    private long startTime = 0l;
    private long endTime = 0L;

    private transient Log log;

	private static final BigDecimal minutesDivisor = MeasureTimeUnit.DIVISORS[MeasureTimeUnit.MINUTES.getCode()];
	private static final BigDecimal hoursDivisor   = MeasureTimeUnit.DIVISORS[MeasureTimeUnit.HOURS.getCode()];
	private static final BigDecimal daysDivisor    = MeasureTimeUnit.DIVISORS[MeasureTimeUnit.DAYS.getCode()];
	
	public ProducerMetricCollector() {
        log().debug("ProducerMetricCollector instantiated");
        messageCount = new AtomicLong();
    }

    public long increment() {
        if (startTime == 0l) {
            startTime = System.nanoTime();
        }
        long retVal = messageCount.incrementAndGet();
        endTime = System.nanoTime();
        return retVal;
    }


    public long getMessageCount() {
        return messageCount.get();
    }

    public String getThroughput(MeasureTimeUnit throughputRate) {
    	BigDecimal durationInNanos = new BigDecimal(endTime - startTime, new MathContext(5));
        BigDecimal durationInDesiredRate = durationInNanos;
        String timeUnitStr = "ns";
        switch (throughputRate) {
            case NANOSECONDS:
                break;
            case MICROSECONDS:
                durationInDesiredRate = durationInNanos.movePointLeft(3);
                timeUnitStr = "micros";
                break;
            case MILLISECONDS:
                durationInDesiredRate = durationInNanos.movePointLeft(6);
                timeUnitStr = "ms";
                break;
            case SECONDS:
                durationInDesiredRate = durationInNanos.movePointLeft(9);
                timeUnitStr = "s";
                break;
            case MINUTES:
                durationInDesiredRate =
                        durationInNanos.movePointLeft(9).divide(minutesDivisor, BigDecimal.ROUND_HALF_UP);
                timeUnitStr = "min";
                break;
            case HOURS:
                BigDecimal hoursMultiplier = minutesDivisor.multiply(hoursDivisor);
                durationInDesiredRate = durationInNanos.movePointLeft(9).divide(hoursMultiplier, BigDecimal.ROUND_HALF_UP);
                timeUnitStr = "hr";
                break;
            case DAYS:
                BigDecimal daysMultiplier = minutesDivisor.multiply(hoursDivisor).multiply(daysDivisor);
                durationInDesiredRate = durationInNanos.movePointLeft(9).divide(daysMultiplier, BigDecimal.ROUND_HALF_UP);
                timeUnitStr = "day";
        }
        BigDecimal throughputInDesiredRate =
                new BigDecimal(messageCount.get(), new MathContext(7))
                        .divide(durationInDesiredRate ,BigDecimal.ROUND_HALF_DOWN);
        log().debug("Throughput calculation complete [messageCount: " + messageCount.get() +
                    ", duration: " + durationInDesiredRate.toPlainString() + timeUnitStr +
                    ", rate: " + throughputInDesiredRate.toPlainString() + " msg/" + timeUnitStr + "]");
        return throughputInDesiredRate.toPlainString();
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
