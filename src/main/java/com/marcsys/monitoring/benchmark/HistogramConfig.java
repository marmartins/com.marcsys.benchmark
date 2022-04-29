package com.marcsys.monitoring.benchmark;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class HistogramConfig {

    @Value("${benchmark.border.mean.count:100}")
    private int maxCount;

    @Value("${benchmark.folder}")
    private String metricsFolder;

    @Value("${benchmark.enabled:false}")
    private boolean enabled;

    @Value("${benchmark.application.name}")
    private String applicationName;

}
