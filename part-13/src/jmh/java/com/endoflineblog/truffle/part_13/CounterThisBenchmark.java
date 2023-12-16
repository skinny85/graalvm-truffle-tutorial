package com.endoflineblog.truffle.part_13;

import org.openjdk.jmh.annotations.Benchmark;

public class CounterThisBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_000_000;

    private static final String COUNTER_CLASS = "" +
            "class Counter { " +
            "    setCount(count) { " +
            "        this.count = count; " +
            "    } " +
            "    getCount() { " +
            "        return this.count; " +
            "    } " +
            "}";

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNTER_CLASS);
        this.truffleContext.eval("ezs", COUNT_WITH_THIS_IN_FOR);

        this.truffleContext.eval("js", COUNTER_CLASS);
        this.truffleContext.eval("js", COUNT_WITH_THIS_IN_FOR);

        this.truffleContext.eval("sl", "" +
                "function countWithThisInFor(n) { " +
                "    counter = new(); " +
                "    i = 1; " +
                "    while (i <= n) { " +
                "        counter.count = i; " +
                "        i = i + 1; " +
                "    } " +
                "    return counter.count; " +
                "}"
        );
    }

    private static final String COUNT_WITH_THIS_IN_FOR = "" +
            "function countWithThisInFor(n) { " +
            "    const counter = new Counter(); " +
            "    for (let i = 1; i <= n; i = i + 1) { " +
//            "        counter.setCount(i); " +
            "        counter.count = i; " +
            "    } " +
//            "    return counter.getCount(); " +
            "    return counter.count; " +
            "}";

    @Benchmark
    public int count_with_this_in_for_ezs() {
        return this.truffleContext.eval("ezs", "countWithThisInFor(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_with_this_in_for_js() {
        return this.truffleContext.eval("js", "countWithThisInFor(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_with_this_in_for_sl() {
        return this.truffleContext.eval("sl", "" +
                "function main() { " +
                "    return countWithThisInFor(" + INPUT + ");" +
                "}"
        ).asInt();
    }
}
