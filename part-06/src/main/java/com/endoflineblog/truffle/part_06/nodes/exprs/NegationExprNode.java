package com.endoflineblog.truffle.part_06.nodes.exprs;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node that represents the negation expression in JavaScript
 * ("unary minus"), like `-3`.
 */
@NodeChild("expr")
public abstract class NegationExprNode extends EasyScriptExprNode {
    @Specialization
    protected int negateInt(int value) {
        return -value;
    }

    @Specialization(replaces = "negateInt")
    protected double negateDouble(double value) {
        return -value;
    }

    @Fallback
    protected double negateUndefined(@SuppressWarnings("unused") Object value) {
        return Double.NaN;
    }
}
