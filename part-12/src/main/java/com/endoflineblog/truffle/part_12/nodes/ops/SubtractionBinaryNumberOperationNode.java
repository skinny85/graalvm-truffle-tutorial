package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class SubtractionBinaryNumberOperationNode extends EasyScriptBinaryNumberOperationNode {
    @Specialization
    protected int subtractIntegers(int arg1, int arg2) {
        return Math.subtractExact(arg1, arg2);
    }

    @Specialization(replaces = "subtractIntegers")
    protected double subtractDoubles(double arg1, double arg2) {
        return arg1 - arg2;
    }

    @Fallback
    protected Object subtractNonNumbers(@SuppressWarnings("unused") Object arg1,
            @SuppressWarnings("unused") Object arg2) {
        return Double.NaN;
    }
}
