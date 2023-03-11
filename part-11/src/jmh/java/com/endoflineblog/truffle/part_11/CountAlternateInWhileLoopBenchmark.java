package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;

public class CountAlternateInWhileLoopBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_400_000;

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG_NO_TEMP);
        this.truffleContext.eval("js", COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG_NO_TEMP);

        this.truffleContext.eval("ezs", COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG_THEN_TEMP);
        this.truffleContext.eval("js", COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG_THEN_TEMP);

        this.truffleContext.eval("ezs", COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG_ELSE_TEMP);
        this.truffleContext.eval("js", COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG_ELSE_TEMP);
    }

    public static final String COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG_NO_TEMP = "" +
            "function countAlternateInWhileLoopNoArgNoTemp() { " +
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

    public static int countAlternateInWhileLoopNoArgNoTemp() {
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
    public int count_alternate_in_while_loop_func_no_arg_no_temp_ezs() {
        return this.truffleContext.eval("ezs", "countAlternateInWhileLoopNoArgNoTemp();").asInt();
    }

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_no_temp_js() {
        return this.truffleContext.eval("js", "countAlternateInWhileLoopNoArgNoTemp();").asInt();
    }

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_no_temp_java() {
        return countAlternateInWhileLoopNoArgNoTemp();
    }

    public static final String COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG_THEN_TEMP = "" +
            "function countAlternateInWhileLoopNoArgThenTemp() { " +
            "    var ret = 0, n = " + INPUT + ", positive = true; " +
            "    while (n > 0) { " +
            "        if (positive) { " +
            "            var s = n; " +
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

    public static int countAlternateInWhileLoopNoArgThenTemp() {
        int ret = 0, n = INPUT;
        boolean positive = true;
        while (n > 0) {
            if (positive) {
                var s = n;
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
    public int count_alternate_in_while_loop_func_no_arg_then_temp_ezs() {
        return this.truffleContext.eval("ezs", "countAlternateInWhileLoopNoArgThenTemp();").asInt();
    }

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_then_temp_js() {
        return this.truffleContext.eval("js", "countAlternateInWhileLoopNoArgThenTemp();").asInt();
    }

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_then_temp_java() {
        return countAlternateInWhileLoopNoArgThenTemp();
    }

    public static final String COUNT_ALTERNATE_WHILE_LOOP_FUNC_NO_ARG_ELSE_TEMP = "" +
            "function countAlternateInWhileLoopNoArgElseTemp() { " +
            "    var ret = 0, n = " + INPUT + ", positive = true; " +
            "    while (n > 0) { " +
            "        if (positive) { " +
            "            ret = ret + n; " +
            "            positive = false; " +
            "        } else { " +
            "            var s = n; " +
            "            ret = ret - n; " +
            "            positive = true; " +
            "        } " +
            "        n = n - 1; " +
            "    } " +
            "    return ret; " +
            "}";

    public static int countAlternateInWhileLoopNoArgElseTemp() {
        int ret = 0, n = INPUT;
        boolean positive = true;
        while (n > 0) {
            if (positive) {
                ret = ret + n;
                positive = false;
            } else {
                var s = n;
                ret = ret - n;
                positive = true;
            }
            n = n - 1;
        }
        return ret;
    }

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_else_temp_ezs() {
        return this.truffleContext.eval("ezs", "countAlternateInWhileLoopNoArgElseTemp();").asInt();
    }

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_else_temp_js() {
        return this.truffleContext.eval("js", "countAlternateInWhileLoopNoArgElseTemp();").asInt();
    }

    @Benchmark
    public int count_alternate_in_while_loop_func_no_arg_else_temp_java() {
        return countAlternateInWhileLoopNoArgElseTemp();
    }
}
