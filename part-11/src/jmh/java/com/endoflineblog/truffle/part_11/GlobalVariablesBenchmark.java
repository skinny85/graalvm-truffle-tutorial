package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;

public class GlobalVariablesBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_400_000;

    public static final String COUNT_IN_WHILE_LOOP_JS = "" +
            "var ret = 0, n = " + INPUT + "; " +
            "while (n > 0) { " +
            "    n = n - 1; " +
            "    ret = ret + 1; " +
            "} " +
            "ret; ";

    @Benchmark
    public int count_in_while_loop_ezs() {
        return this.truffleContext.eval("ezs", COUNT_IN_WHILE_LOOP_JS).asInt();
    }

    @Benchmark
    public int count_in_while_loop_js() {
        return this.truffleContext.eval("js", COUNT_IN_WHILE_LOOP_JS).asInt();
    }

    @Benchmark
    public int count_in_while_loop_java() {
        int ret = 0, n = INPUT;
        while (n > 0) {
            n = n - 1;
            ret = ret + 1;
        }
        return ret;
    }
}
