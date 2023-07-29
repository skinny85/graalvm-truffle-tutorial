package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Specialization;

public abstract class ExternallySpecializingAdditionOpNode extends AbstractAdditionOpNode {
    @Specialization(insertBefore = "addNonNumbers")
    protected int addIntegers(int lvalue, int rvalue) {
        return Math.addExact(lvalue, rvalue);
    }

    @Specialization(replaces = "addIntegers")
    protected double addDoubles(double lvalue, double rvalue) {
        return lvalue + rvalue;
    }
}
