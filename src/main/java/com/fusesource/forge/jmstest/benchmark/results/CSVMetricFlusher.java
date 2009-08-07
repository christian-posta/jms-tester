package com.fusesource.forge.jmstest.benchmark.results;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author  andreasgies
 */
public class CSVMetricFlusher extends AbstractMetricsFlusher {

    private transient Log log;

    private File resultsDir;
    private File outputFile;

    public CSVMetricFlusher() {
    }

    public File getResultsDir() {
        return resultsDir;
    }

    public void setResultsDir(File resultsDir) {
        this.resultsDir = resultsDir;
    }

    public boolean initialiseFlusher() {
        boolean isSetup = true;
        // get file name
        StringBuilder builder = new StringBuilder(resultsDir.getAbsolutePath());
        builder.append(File.separator);
        builder.append("_");
        builder.append(getRunId());
        builder.append("_");
        builder.append(new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()));
        builder.append(".csv");
        // move existing files?
        // create new file
        outputFile = new File(builder.toString());
        
        if (!outputFile.getParentFile().exists()) {
        	outputFile.getParentFile().mkdirs();
        }
        
        if (outputFile.exists()) {
            // weird, overwrite it
            outputFile.renameTo(new File(builder.append(".old").toString()));
        }
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            log().fatal("Unable to initialise output file, benchmark WILL NOT RUN!", e);
            isSetup = false;
        }
        // write header row to it
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(outputFile));
            bw.write(SummarisedMetric.getHeader());
            bw.newLine();
        } catch (IOException e) {
            log().fatal("Unable to writer header to output file, benchmark WILL NOT RUN!", e);
            isSetup = false;
        } finally {
            if (bw != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    // nothing to do here
                }
            }
        }
        if (isSetup) {
            log().info("Benchmark results file initialised [" + builder.toString() + "]");
        } else {
            log().info("Failed to initialised results file [" + builder.toString() + "]");
        }
        return isSetup;
    }

    /**
     * Intention is to run this on a schedule, e.g. flush every 30s
     */
    public void run() {
        flush();
    }

    public void flush() {
        // create OutputStream on File
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(outputFile, true));
            // take copy of metrics from collector
            int writeCount = 0;
            for (SummarisedMetric metric : summarize()) {
                // write each one to file as CSV
                try {
                    bw.write(metric.toCSV());
                    bw.newLine();
                    writeCount++;
                } catch (Exception e) {
                    log().warn("Unexpected exception during flushing of Metric to CSV [" +
                               metric.toCSV() + "]", e);
                }
            }
            log().info("Metrics written to CSV [" + writeCount + "]");
        } catch (IOException e) {
            log().warn("Unable to writer results to output file, benchmark results will NOT be valid!!", e);
        } catch (Exception e) {
            log().warn("Unknown exception during flush", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // nothing to do here
                }
            }
        }
    }

    private Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
