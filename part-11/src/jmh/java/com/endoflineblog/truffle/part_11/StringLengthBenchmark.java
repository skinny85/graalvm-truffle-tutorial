package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;

public class StringLengthBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_000_000;

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", COUNT_WHILE_CHAR_AT_PROP);
        this.truffleContext.eval("js", COUNT_WHILE_CHAR_AT_PROP);

        this.truffleContext.eval("ezs", COUNT_WHILE_CHAR_AT_INDEX);
        this.truffleContext.eval("js", COUNT_WHILE_CHAR_AT_INDEX);
    }

    private static final String COUNT_WHILE_CHAR_AT_PROP = "" +
            "function countWhileCharAtProp(n) { " +
            "    var ret = 0; " +
            "    while (n > 0) { " +
            "        n = n - ('ALU'.charAt(0)).length; " +
            "        ret = ret + 1; " +
            "    } " +
            "    return ret; " +
            "}";

    static int countWhileSubstringJava(int n) {
        int ret = 0;
        while (n > 0) {
            n = n - ("ALU".substring(0, 1) + "ALU".substring(2, 2)).length();
            ret = ret + 1;
        }
        return ret;
    }

    @Benchmark
    public int count_while_char_at_prop_ezs() {
        return this.truffleContext.eval("ezs", "countWhileCharAtProp(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_while_char_at_prop_js() {
        return this.truffleContext.eval("js", "countWhileCharAtProp(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_while_substr_prop_java() {
        return countWhileSubstringJava(INPUT);
    }

    private static final String COUNT_WHILE_CHAR_AT_INDEX = "" +
            "function countWhileCharAtIndex(n) { " +
            "    var ret = 0; " +
            "    while (n > 0) { " +
            "        n = n - ('ALU'['charAt'](0))['length']; " +
            "        ret = ret + 1; " +
            "    } " +
            "    return ret; " +
            "}";

    @Benchmark
    public int count_while_char_at_index_ezs() {
        return this.truffleContext.eval("ezs", "countWhileCharAtIndex(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_while_char_at_index_js() {
        return this.truffleContext.eval("js", "countWhileCharAtIndex(" + INPUT + ");").asInt();
    }
}
