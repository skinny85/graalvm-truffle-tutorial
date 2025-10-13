package com.endoflineblog.truffle.part_16;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * A benchmark measuring the performance of storing state inside class instances.
 * Identical to the class with the same name from part 14.
 */
public class CounterThisBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_000_000;

    private static final String COUNTER_CLASS = "" +
            "class DirectBase extends Object { " +
            "    constructor() { " +
            "        super(); " +
            "        this.count = 0; " +
            "    } " +
            "    increment() { " +
            "        this.count = this.count + 1; " +
            "    } " +
            "    getCount() { " +
            "        return this.count; " +
            "    } " +
            "} " +
            "class DirectLowerMiddle extends DirectBase { " +
            "} " +
            "class DirectUpperMiddle extends DirectLowerMiddle { " +
            "    constructor() { " +
            "        super(); " +
            "    } " +
            "    increment() { " +
            "        return super.increment(); " +
            "    } " +
            "    getCount() { " +
            "        return super.getCount(); " +
            "    } " +
            "} " +
            "class CounterDirect extends DirectUpperMiddle { " +
            "}" +
            " " +
            "class IndexedBase extends Object { " +
            "    constructor() { " +
            "        super(); " +
            "        this['count'] = 0; " +
            "    } " +
            "    increment() { " +
            "        this['count'] = this['count'] + 1; " +
            "    } " +
            "    getCount() { " +
            "        return this['count']; " +
            "    } " +
            "} " +
            "class IndexedLowerMiddle extends IndexedBase { " +
            "} " +
            "class IndexedUpperMiddle extends IndexedLowerMiddle { " +
            "    constructor() { " +
            "        super(); " +
            "    } " +
            "    increment() { " +
            "        return super['increment'](); " +
            "    } " +
            "    getCount() { " +
            "        return super['getCount'](); " +
            "    } " +
            "} " +
            "class CounterIndexed extends IndexedUpperMiddle { " +
            "}";

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNTER_CLASS);
        this.truffleContext.eval("ezs", COUNT_WITH_THIS_IN_FOR);

        this.truffleContext.eval("js", COUNTER_CLASS);
        this.truffleContext.eval("js", COUNT_WITH_THIS_IN_FOR);
    }

    private static final String COUNT_WITH_THIS_IN_FOR = "" +
            "function countWithThisInForDirect(n) { " +
            "    const counter = new CounterDirect(); " +
            "    for (let i = 0; i < n; i = i + 1) { " +
            "        counter.increment(); " +
            "    } " +
            "    return counter.getCount(); " +
            "} " +
            "function countWithThisInForIndexed(n) { " +
            "    const counter = new CounterIndexed(); " +
            "    for (let i = 0; i < n; i = i + 1) { " +
            "        counter['increment'](); " +
            "    } " +
            "    return counter['getCount'](); " +
            "}";

    @Benchmark
    public int count_with_this_in_for_direct_ezs() {
        return this.truffleContext.eval("ezs", "countWithThisInForDirect(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_with_this_in_for_direct_js() {
        return this.truffleContext.eval("js", "countWithThisInForDirect(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_with_this_in_for_indexed_ezs() {
        return this.truffleContext.eval("ezs", "countWithThisInForIndexed(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_with_this_in_for_indexed_js() {
        return this.truffleContext.eval("js", "countWithThisInForIndexed(" + INPUT + ");").asInt();
    }
}
