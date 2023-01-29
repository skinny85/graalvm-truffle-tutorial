package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;

/**
 * Benchmark taken from the
 * <a href="https://chromium.googlesource.com/v8/deps/third_party/benchmarks/+/refs/heads/master/sunspider/string-fasta.js">
 * SunSpider repository</a>.
 */
public class FastaBenchmark extends TruffleBenchmark {
    private static final String FASTA_PROGRAM = "" +
            "var ALU = " +
            "    'GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG' + " +
            "    'GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA' + " +
            "    'CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT' + " +
            "    'ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA' + " +
            "    'GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG' + " +
            "    'AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC' + " +
            "    'AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA';         " +
            "                                                   " +
            "function fastaRepeat(n) { " +
            "    var ret = 0, seqi = 0, lenOut = 60; " +
            "                                        " +
            "    while (n > 0) { " +
            "        if (n < lenOut) " +
            "            lenOut = n; " +
            "                        " +
            "        if (seqi + lenOut < ALU.length) { " +
            "            ret = ret + ALU.substring(seqi, seqi + lenOut).length; " +
            "            seqi = seqi + lenOut; " +
            "        } else { " +
            "            var s = ALU.substring(seqi); " +
            "            seqi = lenOut - s.length; " +
            "            ret = ret + (s + ALU.substring(0, seqi)).length; " +
            "        } " +
            "          " +
            "        n = n - lenOut; " +
            "    } " +
            "      " +
            "    return ret; " +
            "}";

    @Setup
    public void setup() {
        super.setup();
        this.truffleContext.eval("ezs", FASTA_PROGRAM);
        this.truffleContext.eval("js", FASTA_PROGRAM);
    }

    @Benchmark
    public int ezs() {
        return this.truffleContext.eval("ezs", "fastaRepeat(1400000)").asInt();
    }

    @Benchmark
    public int js() {
        return this.truffleContext.eval("js", "fastaRepeat(1400000)").asInt();
    }

    @Benchmark
    public int java() {
        return fastaRepeat(1_400_000);
    }

    static final String ALU = "" +
            "GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG" +
            "GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA" +
            "CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT" +
            "ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA" +
            "GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG" +
            "AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC" +
            "AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA";

    static int fastaRepeat(int n) {
        var ret = 0;
        var seqi = 0;
        var lenOut = 60;

        while (n > 0) {
            if (n < lenOut)
                lenOut = n;

            if (seqi + lenOut < ALU.length()) {
                ret = ret + ALU.substring(seqi, seqi + lenOut).length();
                seqi = seqi + lenOut;
            } else {
                var s = ALU.substring(seqi);
                seqi = lenOut - s.length();
                ret = ret + (s + ALU.substring(0, seqi)).length();
            }

            n = n - lenOut;
        }

        return ret;
    }
}
