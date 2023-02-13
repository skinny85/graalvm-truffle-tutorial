package com.endoflineblog.truffle.part_11;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;

import static com.endoflineblog.truffle.part_11.FastaCode.COUNT_FOR_LOOP_PROGRAM;
import static com.endoflineblog.truffle.part_11.FastaCode.COUNT_WHILE_LOOP_PROGRAM;
import static com.endoflineblog.truffle.part_11.FastaCode.FASTA_PROGRAM;
import static com.endoflineblog.truffle.part_11.FastaCode.FASTA_PROGRAM_NO_SUBSTRING;
import static com.endoflineblog.truffle.part_11.FastaCode.FASTA_PROGRAM_WITHOUT_LENGTH;
import static com.endoflineblog.truffle.part_11.FastaCode.countForLoop;
import static com.endoflineblog.truffle.part_11.FastaCode.countWhileLoop;
import static com.endoflineblog.truffle.part_11.FastaCode.fastaRepeat;
import static com.endoflineblog.truffle.part_11.FastaCode.fastaRepeatNoSubstring;
import static com.endoflineblog.truffle.part_11.FastaCode.fastaRepeatWithoutLength;

/**
 * Benchmark taken from the
 * <a href="https://chromium.googlesource.com/v8/deps/third_party/benchmarks/+/refs/heads/master/sunspider/string-fasta.js">
 * SunSpider repository</a>.
 */
public class FastaBenchmark extends TruffleBenchmark {
    private final int fastaInput = 1_400_000;

    @Setup
    public void setup() {
        super.setup();
        this.truffleContext.eval("ezs", FASTA_PROGRAM);
        this.truffleContext.eval("js", FASTA_PROGRAM);
        this.truffleContext.eval("ezs", FASTA_PROGRAM_NO_SUBSTRING);
        this.truffleContext.eval("js", FASTA_PROGRAM_NO_SUBSTRING);
        this.truffleContext.eval("ezs", FASTA_PROGRAM_WITHOUT_LENGTH);
        this.truffleContext.eval("js", FASTA_PROGRAM_WITHOUT_LENGTH);

        this.truffleContext.eval("ezs", COUNT_FOR_LOOP_PROGRAM);
        this.truffleContext.eval("js", COUNT_FOR_LOOP_PROGRAM);

        this.truffleContext.eval("ezs", COUNT_WHILE_LOOP_PROGRAM);
        this.truffleContext.eval("js", COUNT_WHILE_LOOP_PROGRAM);
    }

    @Benchmark
    public int fasta_repeat_ezs() {
        return this.truffleContext.eval("ezs", "fastaRepeat(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int fasta_repeat_js() {
        return this.truffleContext.eval("js", "fastaRepeat(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int fasta_repeat_java() {
        return fastaRepeat(fastaInput);
    }

    @Benchmark
    public int fasta_repeat_no_substring_ezs() {
        return this.truffleContext.eval("ezs", "fastaRepeatNoSubstring(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int fasta_repeat_no_substring_js() {
        return this.truffleContext.eval("js", "fastaRepeatNoSubstring(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int fasta_repeat_no_substring_java() {
        return fastaRepeatNoSubstring(fastaInput);
    }

    @Benchmark
    public int fasta_repeat_without_length_ezs() {
        return this.truffleContext.eval("ezs", "fastaRepeatWithoutLength(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int fasta_repeat_without_length_js() {
        return this.truffleContext.eval("js", "fastaRepeatWithoutLength(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int fasta_repeat_without_length_java() {
        return fastaRepeatWithoutLength(fastaInput);
    }

    @Benchmark
    public int count_for_loop_ezs() {
        return this.truffleContext.eval("ezs", "countForLoop(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int count_for_loop_js() {
        return this.truffleContext.eval("js", "countForLoop(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int count_for_loop_java() {
        return countForLoop(fastaInput);
    }

    @Benchmark
    public int count_while_loop_ezs() {
        return this.truffleContext.eval("ezs", "countWhileLoop(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int count_while_loop_js() {
        return this.truffleContext.eval("js", "countWhileLoop(" + fastaInput + ")").asInt();
    }

    @Benchmark
    public int count_while_loop_java() {
        return countWhileLoop(fastaInput);
    }
}
