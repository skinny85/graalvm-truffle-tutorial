package com.endoflineblog.truffle.part_09;

import org.graalvm.polyglot.Context;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 20, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsAppend = "-Dgraalvm.locatorDisabled=true")
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class TruffleBenchmark {
    private static final String FIBONACCI_JS_PROGRAM = "" +
            "function fib(n) { " +
            "    if (n > -2) { " +
            "        return Math.abs(n); " +
            "    } " +
            "    return fib(n + 1) + fib(n + 2); " +
            "} " +
            "fib(-20);";

    private Context truffleContext;

    @Setup
    public void setup() {
        this.truffleContext = Context.create();
    }

    @TearDown
    public void tearDown() {
        this.truffleContext.close();
    }

    @Benchmark
    public int fibonacci_recursive_ezs_slow() {
        return this.truffleContext.eval("ezs", FIBONACCI_JS_PROGRAM).asInt();
    }

    @Fork(jvmArgsAppend = {
            "-Dgraalvm.locatorDisabled=false",
            "-Dgraal.Dump=:1",
            "-Dgraal.PrintGraph=Network"
    })
    @Benchmark
    public int fibonacci_recursive_js_slow() {
        return this.truffleContext.eval("js", FIBONACCI_JS_PROGRAM).asInt();
    }

    @Benchmark
    public int fibonacci_recursive_java() {
        return fibonacciRecursive(-20);
    }

    public static int fibonacciRecursive(int n) {
        return n > -2
                ? Math.abs(n)
                : fibonacciRecursive(n + 1) + fibonacciRecursive(n + 2);
    }
}
