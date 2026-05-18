package com.endoflineblog.truffle.part_16;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * A benchmark that measures the performance of the EasyScript closure implementation.
 */
public class ClosureBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_000_000;

    private static final String COUNT_DOWN_IMPLEMENTATIONS = "" +
            "function countDownBaseline(n) { " +
            "    let count = 0; " +
            "    for (let i = n; i > 0; i = i - 1) { " +
            "        count = count + 1; " +
            "    } " +
            "    return count; " +
            "} " +
            "function countDownNested(n) { " +
            "    function countDownInternal(n) { " +
            "        let count = 0; " +
            "        for (let i = n; i > 0; i = i - 1) { " +
            "            count = count + 1; " +
            "        } " +
            "        return count; " +
            "    } " +
            "    return countDownInternal(n); " +
            "} " +
            "function countDownClosure(n) { " +
            "    let count = 0; " +
            "    function countDownInternal() { " +
            "        for (let i = n; i > 0; i = i - 1) { " +
            "            count = count + 1; " +
            "        } " +
            "    } " +
            "    countDownInternal(); " +
            "    return count; " +
            "} " +
            "function countDownLambda(n) { " +
            "    let count = 0; " +
            "    (() => { " +
            "        for (let i = n; i > 0; i = i - 1) { " +
            "            count = count + 1; " +
            "        } " +
            "    })(); " +
            "    return count; " +
            "}";

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNT_DOWN_IMPLEMENTATIONS);

        this.truffleContext.eval("js", COUNT_DOWN_IMPLEMENTATIONS);
    }

    @Benchmark
    public int count_down_baseline_ezs() {
        return this.truffleContext.eval("ezs", "countDownBaseline(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_down_baseline_js() {
        return this.truffleContext.eval("js", "countDownBaseline(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_down_nested_ezs() {
        return this.truffleContext.eval("ezs", "countDownNested(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_down_nested_js() {
        return this.truffleContext.eval("js", "countDownNested(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_down_closure_ezs() {
        return this.truffleContext.eval("ezs", "countDownClosure(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_down_closure_js() {
        return this.truffleContext.eval("js", "countDownClosure(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_down_lambda_ezs() {
        return this.truffleContext.eval("ezs", "countDownLambda(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_down_lambda_js() {
        return this.truffleContext.eval("js", "countDownLambda(" + INPUT + ");").asInt();
    }
}
