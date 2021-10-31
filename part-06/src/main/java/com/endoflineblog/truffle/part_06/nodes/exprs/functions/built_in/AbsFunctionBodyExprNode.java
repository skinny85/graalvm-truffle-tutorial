package com.endoflineblog.truffle.part_06.nodes.exprs.functions.built_in;

import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChild("argument")
public abstract class AbsFunctionBodyExprNode extends EasyScriptExprNode {
    @Specialization
    protected int intAbs(int argument) {
        return argument >= 0 ? argument : -argument;
    }

    @Specialization
    protected double doubleAbs(double argument) {
        return argument >= 0 ? argument : -argument;
    }

    /**
     * It's always possible to get called with 'undefined' passed as an argument,
     * or even another function.
     * Simply return NaN in all those cases.
     */
    @Fallback
    protected double nonNumberAbs(@SuppressWarnings("unused") Object argument) {
        return Double.NaN;
    }
}
