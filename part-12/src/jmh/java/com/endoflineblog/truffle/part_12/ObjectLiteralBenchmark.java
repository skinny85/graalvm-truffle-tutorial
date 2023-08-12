package com.endoflineblog.truffle.part_12;

import org.openjdk.jmh.annotations.Benchmark;

public class ObjectLiteralBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_000_000;

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNT_FOR_OBJECT);
        this.truffleContext.eval("js", COUNT_FOR_OBJECT);
    }

    private static final String COUNT_FOR_OBJECT = "" +
            "function countForObject(n) { " +
            "    let ret = 0; " +
            "    for (let i = 0; i < n; i = i + 1) { " +
            "        let obj = { ['field']: i }; " +
            "        ret = ret + obj.field; " +
            "    } " +
            "    return ret; " +
            "}";

    @Benchmark
    public double count_for_object_ezs() {
        return this.truffleContext.eval("ezs", "countForObject(" + INPUT + ");").asDouble();
    }

    @Benchmark
    public double count_for_object_js() {
        return this.truffleContext.eval("js", "countForObject(" + INPUT + ");").asDouble();
    }
}
