package com.endoflineblog.truffle.part_16;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * A benchmark that uses an exception to stop a loop from iterating.
 */
public class CountdownBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_000_000;

    private static final String COUNTDOWN = "" +
            "class Countdown { " +
            "    constructor(start) { " +
            "        this.count = start; " +
            "    } " +
            "    decrement() { " +
            "        if (this.count <= 0) { " +
            "            throw new Error('countdown has completed'); " +
            "        } " +
            "        this.count = this.count - 1; " +
            "    } " +
            "} " +
            "function countdown(n) { " +
            "    const countdown = new Countdown(n); " +
            "    let ret = 0; " +
            "    for (;;) { " +
            "        try { " +
            "            countdown.decrement(); " +
            "            ret = ret + 1; " +
            "        } catch (e) { " +
            "            break; " +
            "        } " +
            "    } " +
            "    return ret; " +
            "} ";

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNTDOWN);

        this.truffleContext.eval("js", COUNTDOWN);
    }

    @Benchmark
    public int count_down_with_exception_ezs() {
        return this.truffleContext.eval("ezs", "countdown(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_down_with_exception_js() {
        return this.truffleContext.eval("js", "countdown(" + INPUT + ");").asInt();
    }
}
