package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;

public class StringLengthBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_400_000;

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNT_IN_WHILE_LOOP_FUNC_NO_ARG);
        this.truffleContext.eval("js", COUNT_IN_WHILE_LOOP_FUNC_NO_ARG);

        this.truffleContext.eval("ezs", COUNT_IN_WHILE_LOOP_FUNC_WITH_ARG);
        this.truffleContext.eval("js", COUNT_IN_WHILE_LOOP_FUNC_WITH_ARG);
    }

    public static final String COUNT_IN_WHILE_LOOP_FUNC_NO_ARG = "var ALU = '" + FastaCode.ALU + "'; " +
            "function countInWhileLoopNoArg() { " +
            "    var ret = 0, n = " + INPUT + "; " +
            "    while (n > 0) { " +
            "        n = n - 'A'.length; " +
            "        ret = ret + 1; " +
            "    } " +
            "    return ret; " +
            "}";

    static int countInWhileLoopNoArg() {
        int ret = 0, n = INPUT;
        while (n > 0) {
//            n = n - 1;
//            n = n - FastaCode.ALU.substring(0, 1).length();
            n = n - "a".length();
            ret = ret + 1;
        }
        return ret;
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

    private static final String COUNT_IN_WHILE_LOOP_FUNC_WITH_ARG = "" +
            "function countInWhileLoopWithArg(n) { " +
            "    var ret = 0; " +
            "    while (n > 0) { " +
            "        n = n  - 'B'.length; " +
            "        ret = ret + 1; " +
            "    } " +
            "    return ret; " +
            "} ";

    static int countInWhileLoopWithArg(int n) {
        int ret = 0;
        while (n > 0) {
//            n = n - 1;
//            n = n - FastaCode.ALU.substring(0, 1).length();
            n = n - "a".length();
            ret = ret + 1;
        }
        return ret;
    }

    @Benchmark
    public int count_in_while_loop_func_with_arg_ezs() {
        return this.truffleContext.eval("ezs", "countInWhileLoopWithArg(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_in_while_loop_func_with_arg_js() {
        return this.truffleContext.eval("js", "countInWhileLoopWithArg(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_in_while_loop_func_with_arg_java() {
        return countInWhileLoopWithArg(INPUT);
    }
}
