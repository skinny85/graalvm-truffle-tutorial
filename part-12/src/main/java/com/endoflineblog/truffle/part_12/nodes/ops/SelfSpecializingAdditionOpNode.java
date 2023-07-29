package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Specialization;

public abstract class SelfSpecializingAdditionOpNode extends AbstractAdditionOpNode {
    @Specialization(rewriteOn = ArithmeticException.class, insertBefore = "addNonNumbers")
    protected int addIntegers(int lvalue, int rvalue) {
        return Math.addExact(lvalue, rvalue);
    }

    @Specialization(replaces = "addIntegers")
    protected double addDoubles(double lvalue, double rvalue) {
        return lvalue + rvalue;
    }
}
