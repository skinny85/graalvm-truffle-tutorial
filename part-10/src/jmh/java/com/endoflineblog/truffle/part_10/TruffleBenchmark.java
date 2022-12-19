package com.endoflineblog.truffle.part_10;

import org.graalvm.polyglot.Context;
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

/**
 * The common superclass of all JMH benchmarks.
 * Identical to the class with the same name from part 9.
 */
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsAppend = {
        "-Dgraalvm.locatorDisabled=true",
        "--add-exports",
        "org.graalvm.truffle/com.oracle.truffle.api.staticobject=ALL-UNNAMED"
})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public abstract class TruffleBenchmark {
    protected Context truffleContext;

    @Setup
    public void setup() {
        this.truffleContext = Context.create();
    }

    @TearDown
    public void tearDown() {
        this.truffleContext.close();
    }
}
