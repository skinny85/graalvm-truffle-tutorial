package com.endoflineblog.truffle.part_09;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
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
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class TruffleBenchmark {
    private static final String fibonacciJsProgram = "" +
            "function fib(n) { " +
            "    if (n > -2) { " +
            "        return Math.abs(n); " +
            "    } " +
            "    return fib(n + 1) + fib(n + 2); " +
            "} " +
            "fib(-20) " +
            "";

    private Context truffleContext;
    private Value fibProgramValue;

    @Setup
    public void setup() {
        this.truffleContext = Context.create();
        Source fibProgram = Source.create("ezs", fibonacciJsProgram);
        this.fibProgramValue = this.truffleContext.parse(fibProgram);
    }

    @TearDown
    public void tearDown() {
        this.truffleContext.close();
    }

    @Benchmark
    public int fibonacci_recursive_truffle_slow() {
        return this.truffleContext.eval("ezs", fibonacciJsProgram).asInt();
    }

    @Benchmark
    public int fibonacci_recursive_truffle_fast() {
        return this.fibProgramValue.execute().asInt();
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
