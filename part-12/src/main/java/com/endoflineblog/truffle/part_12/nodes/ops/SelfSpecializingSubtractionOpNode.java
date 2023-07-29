package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Specialization;

public abstract class SelfSpecializingSubtractionOpNode extends AbstractSubtractionOpNode {
    @Specialization(rewriteOn = ArithmeticException.class, insertBefore = "subtractNonNumbers")
    protected int subtractIntegers(int lvalue, int rvalue) {
        return Math.subtractExact(lvalue, rvalue);
    }

    @Specialization(replaces = "subtractIntegers")
    protected double subtractDoubles(double lvalue, double rvalue) {
        return lvalue - rvalue;
    }
}
