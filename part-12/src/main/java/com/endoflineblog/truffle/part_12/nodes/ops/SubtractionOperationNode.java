package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class SubtractionOperationNode extends EasyScriptBinaryOperationNode {
    @Specialization
    protected int subtractIntegers(int lvalue, int rvalue) {
        return Math.subtractExact(lvalue, rvalue);
    }

    @Specialization(replaces = "subtractIntegers")
    protected double subtractDoubles(double lvalue, double rvalue) {
        return lvalue - rvalue;
    }

    @Fallback
    protected Object subtractNonNumbers(@SuppressWarnings("unused") Object lvalue,
            @SuppressWarnings("unused") Object rvalue) {
        return Double.NaN;
    }
}
