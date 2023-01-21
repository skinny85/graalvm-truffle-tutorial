package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * A benchmark that uses the naive implementation of the Fibonacci function.
 * Identical to the class with the same name from part 9.
 */
public class FibonacciBenchmark extends TruffleBenchmark {
    private static final String FIBONACCI_JS_FUNCTION = "" +
            "function fib(n) { " +
            "    if (n < 2) { " +
            "        return 1; " +
            "    } " +
            "    return fib(n - 1) + fib(n - 2); " +
            "} ";
    private static final String FIBONACCI_JS_PROGRAM = FIBONACCI_JS_FUNCTION + "fib(20);";

    @Benchmark
    public int recursive_ezs_eval() {
        return this.truffleContext.eval("ezs", FIBONACCI_JS_PROGRAM).asInt();
    }

    @Benchmark
    public int recursive_js_eval() {
        return this.truffleContext.eval("js", FIBONACCI_JS_PROGRAM).asInt();
    }

    @Benchmark
    public int recursive_sl_eval() {
        return this.truffleContext.eval("sl", FIBONACCI_JS_FUNCTION +
                "function main() { " +
                "    return fib(20); " +
                "} ").asInt();
    }

    @Benchmark
    public int recursive_java() {
        return fibonacciRecursive(20);
    }

    public static int fibonacciRecursive(int n) {
        return n < 2
                ? 1
                : fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }
}
