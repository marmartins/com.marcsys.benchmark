package com.marcsys.monitoring.benchmark;

import lombok.extern.slf4j.Slf4j;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.marcsys.monitoring.benchmark.HDRConstants.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
public class HistogramHolder {

    public static final String OS_NAME = "os.name";
    public static final String OS_NAME_WINDOWS = "Windows";
    public static final String WINDOWS_DEFAULT_FOLDER = "C:/";
    public static final String UNIX_DEFAULT_FOLDER = "/var/log/";
    private long leap;
    private String name;
    private HistogramConfig config;
    private final Histogram histogram;
    private final HistogramLogWriter logWriter;

    public HistogramHolder(HistogramConfig config, String name) throws Exception {
        this.name = name != null ? name : config.getApplicationName();
        this.config = config;
        configureMetricsFolder(config);
        long now = System.currentTimeMillis();
        logWriter = getLogWriter(now);
        histogram = getHistogram(now);
    }

    private void configureMetricsFolder(HistogramConfig config) throws IOException {
        String osName = System.getProperty(OS_NAME);
        if(!hasText(config.getMetricsFolder())) {
            if (osName.startsWith(OS_NAME_WINDOWS)) {
                config.setMetricsFolder(WINDOWS_DEFAULT_FOLDER + config.getApplicationName());
            } else {
                config.setMetricsFolder(UNIX_DEFAULT_FOLDER + config.getApplicationName());
            }
        }
        Path path = Paths.get(config.getMetricsFolder());
        if (Files.isDirectory(path) && !Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public HistogramLogWriter getLogWriter(long now) throws FileNotFoundException {

        String path = (config.getMetricsFolder().endsWith("/")
                ? config.getMetricsFolder()
                : config.getMetricsFolder() + "/")
                .concat(name)
                .concat(SUFIX_NAME)
                .concat(FILE_EXTENSION);
        log.info("Creating HistogramLogWriter in [{}]", path);
        File file = new File(path);
        file.getParentFile().mkdirs();

        HistogramLogWriter writer = new HistogramLogWriter(path);

        writer.outputLogFormatVersion();
        writer.outputStartTime(now);
        writer.setBaseTime(now);
        writer.outputLegend();
        return writer;
    }

    public Histogram getHistogram(long now) {
        Histogram histogram = new Histogram(HIGHEST_TRACKABLE_VALUE, PRECISION);
        histogram.setAutoResize(true);
        histogram.setStartTimeStamp(now);
        return histogram;
    }

    public long getLeap() {
        return leap;
    }

    public void setLeap(long leap) {
        this.leap = leap;
    }

    public String getName() {
        return name;
    }

    public void recordValue(long value) {
        if (value < 0) {
            log.warn("Invalid value [{}]. Histogram only perform positive values.", value);
            return;
        }
        try {
            log.info("Recording ... {}", value);
            histogram.recordValue(value);
            // After achieve the max samples write a log
            if (histogram.getTotalCount() >= config.getMaxCount()) {
                log.info("Resetting histogram after {} iterations", histogram.getTotalCount());
//                long now = System.currentTimeMillis();
                // Finish the histogram
                histogram.setEndTimeStamp(System.currentTimeMillis());
                // Write the accumulated value
                logWriter.outputIntervalHistogram(histogram);
                // Reset the histogram
                histogram.reset();
                histogram.setStartTimeStamp(System.currentTimeMillis());
            }
        }catch (Exception e) {
            log.warn("Error while saving record [{}] to histogram.", value, e);
        }
    }
}
