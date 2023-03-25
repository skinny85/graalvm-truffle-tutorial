package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;

public class StringLengthBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_000_000;

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNT_WHILE_CHAR_AT_DIRECT_PROP);
        this.truffleContext.eval("js", COUNT_WHILE_CHAR_AT_DIRECT_PROP);

        this.truffleContext.eval("ezs", COUNT_WHILE_CHAR_AT_INDEX_PROP);
        this.truffleContext.eval("js", COUNT_WHILE_CHAR_AT_INDEX_PROP);
    }

    private static final String COUNT_WHILE_CHAR_AT_DIRECT_PROP = "" +
            "function countWhileCharAtDirectProp(n) { " +
            "    var ret = 0; " +
            "    while (n > 0) { " +
            "        n = n - ('a'.charAt(0) + ''.charAt()).length; " +
            "        ret = ret + 1; " +
            "    } " +
            "    return ret; " +
            "}";

    @Benchmark
    public int count_while_char_at_direct_prop_ezs() {
        return this.truffleContext.eval("ezs", "countWhileCharAtDirectProp(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_while_char_at_direct_prop_js() {
        return this.truffleContext.eval("js", "countWhileCharAtDirectProp(" + INPUT + ");").asInt();
    }

    private static final String COUNT_WHILE_CHAR_AT_INDEX_PROP = "" +
            "function countWhileCharAtIndexProp(n) { " +
            "    var ret = 0; " +
            "    while (n > 0) { " +
            "        n = n - ('a'['charAt'](0) + ''['charAt']())['length']; " +
            "        ret = ret + 1; " +
            "    } " +
            "    return ret; " +
            "}";

    @Benchmark
    public int count_while_char_at_index_prop_ezs() {
        return this.truffleContext.eval("ezs", "countWhileCharAtIndexProp(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_while_char_at_index_prop_js() {
        return this.truffleContext.eval("js", "countWhileCharAtIndexProp(" + INPUT + ");").asInt();
    }
}
