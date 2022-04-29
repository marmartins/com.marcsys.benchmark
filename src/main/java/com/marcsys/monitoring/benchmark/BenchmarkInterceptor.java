package com.marcsys.monitoring.benchmark;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@ConditionalOnExpression("${benchmark.enabled:false}")
public class BenchmarkInterceptor {

    private final HistogramController histogramController;

    @Autowired
    public BenchmarkInterceptor(HistogramController histogramController) {
        this.histogramController = histogramController;
    }

    @Around("@annotation(com.marcsys.monitoring.benchmark.annotations.Benchmark)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String name = joinPoint.getSignature().toString()
                .replaceAll("\\s", "_")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "");
        histogramController.start(name);
        Object result = joinPoint.proceed();
        histogramController.finish(name);
        return result;
    }

}
