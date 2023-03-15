package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;

public class StringLengthBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_000_000;

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNT_WHILE_SUBSTR_LEN_PROP);
        this.truffleContext.eval("js", COUNT_WHILE_SUBSTR_LEN_PROP);

        this.truffleContext.eval("ezs", COUNT_WHILE_SUBSTR_LEN_INDEX);
        this.truffleContext.eval("js", COUNT_WHILE_SUBSTR_LEN_INDEX);
    }

    private static final String COUNT_WHILE_SUBSTR_LEN_PROP = "" +
            "function countWhileSubstrLenProp(n) { " +
            "    var ret = 0; " +
            "    while (n > 0) { " +
            "        n = n - 'ALU'.substring(0, 1).length; " +
            "        ret = ret + 1; " +
            "    } " +
            "    return ret; " +
            "}";

    static int countWhileJava(int n) {
        int ret = 0;
        while (n > 0) {
            n = n - "ALU".substring(0, 1).length();
            ret = ret + 1;
        }
        return ret;
    }

    @Benchmark
    public int count_while_substr_len_prop_ezs() {
        return this.truffleContext.eval("ezs", "countWhileSubstrLenProp(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_while_substr_len_prop_js() {
        return this.truffleContext.eval("js", "countWhileSubstrLenProp(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_while_substr_len_prop_java() {
        return countWhileJava(INPUT);
    }

    private static final String COUNT_WHILE_SUBSTR_LEN_INDEX = "" +
            "function countWhileSubstrLenIndex(n) { " +
            "    var ret = 0; " +
            "    while (n > 0) { " +
            "        n = n - 'ALU'['substring'](0, 1)['length']; " +
            "        ret = ret + 1; " +
            "    } " +
            "    return ret; " +
            "}";

    @Benchmark
    public int count_while_substr_len_index_ezs() {
        return this.truffleContext.eval("ezs", "countWhileSubstrLenIndex(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_while_substr_len_index_js() {
        return this.truffleContext.eval("js", "countWhileSubstrLenIndex(" + INPUT + ");").asInt();
    }
}
