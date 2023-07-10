package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class AdditionBinaryNumberOperationNode extends EasyScriptBinaryNumberOperationNode {
    @Specialization
    protected int addIntegers(int arg1, int arg2) {
        return Math.addExact(arg1, arg2);
    }

    @Specialization(replaces = "addIntegers")
    protected double addDoubles(double arg1, double arg2) {
        return arg1 + arg2;
    }

    @Fallback
    protected Object addNonNumbers(@SuppressWarnings("unused") Object arg1,
            @SuppressWarnings("unused") Object arg2) {
        return Double.NaN;
    }
}
