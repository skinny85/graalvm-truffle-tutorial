package com.endoflineblog.truffle.part_12.nodes.ops;

import com.oracle.truffle.api.dsl.Fallback;

public abstract class AbstractAdditionOpNode extends EasyScriptBinaryOpNode {
    @Fallback
    protected Object addNonNumbers(@SuppressWarnings("unused") Object lvalue,
            @SuppressWarnings("unused") Object rvalue) {
        return Double.NaN;
    }
}
