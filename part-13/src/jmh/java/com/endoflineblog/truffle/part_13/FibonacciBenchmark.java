package com.endoflineblog.truffle.part_13;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * A simple benchmark for calling an instance method of a user-defined class.
 */
public class FibonacciBenchmark extends TruffleBenchmark {
    private static final String FIBONACCI_JS_FUNCTION = "" +
            "function fib(n) { " +
            "    if (n > -2) { " +
            "        return Math.abs(n); " +
            "    } " +
            "    return fib(n + 1) + fib(n + 2); " +
            "}";
    private static final String FIBONACCI_JS_PROGRAM = FIBONACCI_JS_FUNCTION + "fib(-20);";

    @Benchmark
    public int recursive_ezs_eval() {
        return this.truffleContext.eval("ezs", FIBONACCI_JS_PROGRAM).asInt();
    }

    @Benchmark
    public int recursive_js_eval() {
        return this.truffleContext.eval("js", FIBONACCI_JS_PROGRAM).asInt();
    }
}
