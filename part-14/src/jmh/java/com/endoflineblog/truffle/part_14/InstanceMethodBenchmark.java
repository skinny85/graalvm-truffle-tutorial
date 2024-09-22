package com.endoflineblog.truffle.part_14;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * A simple benchmark for calling an instance method of a user-defined class.
 * Identical to the class with the same name from part 13.
 */
public class InstanceMethodBenchmark extends TruffleBenchmark {
    private static final int INPUT = 1_000_000;

    private static final String ADDER_CLASS = "" +
            "class Adder { " +
            "    add(a, b) { " +
            "        return a + b; " +
            "     } " +
            "}";

    @Override
    public void setup() {
        super.setup();

        this.truffleContext.eval("ezs", ADDER_CLASS);
        this.truffleContext.eval("ezs", COUNT_METHOD_PROP_ALLOC_INSIDE_FOR);
        this.truffleContext.eval("ezs", COUNT_METHOD_PROP_ALLOC_OUTSIDE_FOR);

        this.truffleContext.eval("js", ADDER_CLASS);
        this.truffleContext.eval("js", COUNT_METHOD_PROP_ALLOC_INSIDE_FOR);
        this.truffleContext.eval("js", COUNT_METHOD_PROP_ALLOC_OUTSIDE_FOR);
    }

    private static final String COUNT_METHOD_PROP_ALLOC_INSIDE_FOR = "" +
            "function countMethodPropAllocInsideFor(n) { " +
            "    var ret = 0; " +
            "    for (let i = 0; i < n; i = i + 1) { " +
            "        ret = new Adder().add(ret, 1); " +
            "    } " +
            "    return ret; " +
            "}";

    @Benchmark
    public int count_method_prop_alloc_inside_for_ezs() {
        return this.truffleContext.eval("ezs", "countMethodPropAllocInsideFor(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_method_prop_alloc_inside_for_js() {
        return this.truffleContext.eval("js", "countMethodPropAllocInsideFor(" + INPUT + ");").asInt();
    }

    private static final String COUNT_METHOD_PROP_ALLOC_OUTSIDE_FOR = "" +
            "function countMethodPropAllocOutsideFor(n) { " +
            "    var ret = 0; " +
            "    const adder = new Adder(); " +
            "    for (let i = 0; i < n; i = i + 1) { " +
            "        ret = adder.add(ret, 1); " +
            "    } " +
            "    return ret; " +
            "}";

    @Benchmark
    public int count_method_prop_alloc_outside_for_ezs() {
        return this.truffleContext.eval("ezs", "countMethodPropAllocOutsideFor(" + INPUT + ");").asInt();
    }

    @Benchmark
    public int count_method_prop_alloc_outside_for_js() {
        return this.truffleContext.eval("js", "countMethodPropAllocOutsideFor(" + INPUT + ");").asInt();
    }
}
