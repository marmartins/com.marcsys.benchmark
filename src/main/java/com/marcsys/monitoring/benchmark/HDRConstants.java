package com.marcsys.monitoring.benchmark;

import java.util.concurrent.TimeUnit;

public final class HDRConstants {
    private HDRConstants() {
    }

    public static int  PRECISION = 2;
    public static long HIGHEST_TRACKABLE_VALUE = TimeUnit.SECONDS.toMillis(1);
    public static String SUFIX_NAME = "-metrics";
    public static String FILE_EXTENSION = ".perf";

}
