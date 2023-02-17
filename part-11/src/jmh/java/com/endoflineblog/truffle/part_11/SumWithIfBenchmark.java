package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;

public class SumWithIfBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_400_000;

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG);
        this.truffleContext.eval("js", COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG);
    }

    public static final String COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG = "" +
            "function countAlternateInWhileLoopNoArg() { " +
            "    var ret = 0, n = " + INPUT + ", positive = true; " +
            "    while (n > 0) { " +
            "        if (positive) { " +
            "            ret = ret + n; " +
            "            positive = false; " +
            "        } else { " +
            "            ret = ret - n; " +
            "            positive = true; " +
            "        } " +
            "        n = n - 1; " +
            "    } " +
            "    return ret; " +
            "}";

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_ezs() {
        return this.truffleContext.eval("ezs", "countAlternateInWhileLoopNoArg();").asInt();
    }

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_js() {
        return this.truffleContext.eval("js", "countAlternateInWhileLoopNoArg();").asInt();
    }

    public static int countAlternateInWhileLoopNoArg() {
        int ret = 0, n = INPUT;
        boolean positive = true;
        while (n > 0) {
            if (positive) {
                ret = ret + n;
                positive = false;
            } else {
                ret = ret - n;
                positive = true;
            }
            n = n - 1;
        }
        return ret;
    }

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_java() {
        return countAlternateInWhileLoopNoArg();
    }
}
