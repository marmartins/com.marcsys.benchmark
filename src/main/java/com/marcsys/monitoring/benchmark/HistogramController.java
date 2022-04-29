package com.marcsys.monitoring.benchmark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class HistogramController {

    private final HistogramConfig config;
    private Map<String, HistogramHolder> histogramPool = new HashMap<>();

    public HistogramController(HistogramConfig config) {
        this.config = config;
    }

    public void create(String name) {
        log.info("Creating Histogram configuration for: [{}]", name);
        if (isNull(name)) {
            throw new IllegalArgumentException("Histogram name can't be null");
        }
        try {
            HistogramHolder histogramHolder = new HistogramHolder(config, name);
            histogramPool.put(histogramHolder.getName(), histogramHolder);
        } catch (Exception e) {
            log.warn("Error to create HistogramHolder", e);
        }
    }

    public boolean  start() {
        if (histogramPool.size() > 1) {
            log.warn("HistogramHolder name informed");
            return false;
        }
        return start(config.getApplicationName());
    }

    public boolean  start(String name) {
        HistogramHolder histogramHolder = histogramPool.get(name);
        if (isNull(histogramHolder)) {
            create(name);
            histogramHolder = histogramPool.get(name);
            if (isNull(histogramHolder)) {
                log.warn("Invalid histogram name to start: [{}]", name);
                return false;
            }
        }
        histogramHolder.setLeap(System.nanoTime());
        return true;
    }

    public boolean  finish() {
        if (histogramPool.size() > 1) {
            log.warn("HistogramHolder name informed");
            return false;
        }
        log.info("Writing metrics...");
        return finish(config.getApplicationName());
    }

    public boolean  finish(String name) {
        HistogramHolder histogramHolder = histogramPool.get(name);
        if (isNull(histogramHolder)) {
            log.warn("Invalid histogram name to finish: [{}]", name);
            return false;
        }
        histogramHolder.recordValue(System.nanoTime() - histogramHolder.getLeap());
        return true;
    }

}
