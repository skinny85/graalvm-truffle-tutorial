package com.endoflineblog.truffle.part_10.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_10.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node that represents the negation expression in JavaScript
 * ("unary minus"), like {@code -3}.
 * Identical to the class with the same name from part 8.
 */
@NodeChild("expr")
public abstract class NegationExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int negateInt(int value) {
        // Integer.MIN_VALUE is too big to fit negated into an int
        return Math.negateExact(value);
    }

    @Specialization(replaces = "negateInt")
    protected double negateDouble(double value) {
        return -value;
    }

    /**
     * Same as with {@link AdditionExprNode},
     * we return NaN when attempting to negate something that's not a number.
     */
    @Fallback
    protected double negateNonNumber(@SuppressWarnings("unused") Object value) {
        return Double.NaN;
    }
}
