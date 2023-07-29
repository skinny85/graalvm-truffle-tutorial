package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Fallback;

public abstract class AbstractSubtractionOpNode extends EasyScriptBinaryOpNode {
    @Fallback
    protected Object subtractNonNumbers(@SuppressWarnings("unused") Object lvalue,
            @SuppressWarnings("unused") Object rvalue) {
        return Double.NaN;
    }
}
