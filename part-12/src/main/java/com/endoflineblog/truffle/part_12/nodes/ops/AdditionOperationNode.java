package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class AdditionOperationNode extends EasyScriptBinaryOperationNode {
    @Specialization
    protected int addIntegers(int lvalue, int rvalue) {
        return Math.addExact(lvalue, rvalue);
    }

    @Specialization(replaces = "addIntegers")
    protected double addDoubles(double lvalue, double rvalue) {
        return lvalue + rvalue;
    }

    @Fallback
    protected Object addNonNumbers(@SuppressWarnings("unused") Object lvalue,
            @SuppressWarnings("unused") Object rvalue) {
        return Double.NaN;
    }
}
