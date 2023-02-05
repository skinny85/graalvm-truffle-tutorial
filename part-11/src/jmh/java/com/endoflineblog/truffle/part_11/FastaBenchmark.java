package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;

import static com.endoflineblog.truffle.part_11.FastaCode.FASTA_PROGRAM;
import static com.endoflineblog.truffle.part_11.FastaCode.fastaRepeat;

/**
 * Benchmark taken from the
 * <a href="https://chromium.googlesource.com/v8/deps/third_party/benchmarks/+/refs/heads/master/sunspider/string-fasta.js">
 * SunSpider repository</a>.
 */
public class FastaBenchmark extends TruffleBenchmark {
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
}
