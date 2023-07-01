package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class IncrementOperationNode extends EasyScriptOperationNode {
    @Specialization
    protected int incrementInt(int value) {
        return Math.addExact(value, 1);
    }

    @Specialization(replaces = "incrementInt")
    protected double incrementDouble(double value) {
        return value + 1;
    }

    @Fallback
    protected Object incrementNonNumber(@SuppressWarnings("unused") Object value) {
        return Double.NaN;
    }
}
