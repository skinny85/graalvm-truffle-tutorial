package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;

public class LocalVariablesBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_400_000;

    private static final String COUNT_IN_WHILE_LOOP_FUNC_NO_ARG = "" +
            "function countInWhileLoopNoArg() { " +
            "    var ret = 0, n = " + INPUT + "; " +
            "    while (n > 0) { " +
            "        n = n - 1; " +
            "        ret = ret + 1; " +
            "    } " +
            "    return ret; " +
            "} ";

    @Override
    public void setup() {
        super.setup();
        this.truffleContext.eval("ezs", COUNT_IN_WHILE_LOOP_FUNC_NO_ARG);
        this.truffleContext.eval("js", COUNT_IN_WHILE_LOOP_FUNC_NO_ARG);
    }

    @Benchmark
    public int count_in_while_loop_func_no_arg_ezs() {
        return this.truffleContext.eval("ezs", "countInWhileLoopNoArg();").asInt();
    }

    @Benchmark
    public int count_in_while_loop_func_no_arg_js() {
        return this.truffleContext.eval("js", "countInWhileLoopNoArg();").asInt();
    }

    @Benchmark
    public int count_in_while_loop_func_no_arg_java() {
        return countInWhileLoopNoArg();
    }

    static int countInWhileLoopNoArg() {
        int ret = 0, n = INPUT;
        while (n > 0) {
            n = n - 1;
            ret = ret + 1;
        }
        return ret;
    }
}
