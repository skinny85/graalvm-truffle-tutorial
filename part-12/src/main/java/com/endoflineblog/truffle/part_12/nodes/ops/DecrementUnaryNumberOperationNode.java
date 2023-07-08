package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class DecrementUnaryNumberOperationNode extends EasyScriptUnaryNumberOperationNode {
    @Specialization
    protected int decrementInt(int value) {
        return Math.subtractExact(value, 1);
    }

    @Specialization(replaces = "decrementInt")
    protected double decrementDouble(double value) {
        return value - 1;
    }

    @Fallback
    protected Object decrementNonNumber(@SuppressWarnings("unused") Object value) {
        return Double.NaN;
    }
}
