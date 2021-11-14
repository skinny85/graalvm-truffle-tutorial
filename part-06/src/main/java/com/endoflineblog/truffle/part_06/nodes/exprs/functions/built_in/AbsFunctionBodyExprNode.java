package com.endoflineblog.truffle.part_06.nodes.exprs.functions.built_in;

import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * An expression Node that represents the implementation of the
 * {@code Math.abs()} JavaScript function.
 *
 * Note that we don't make it inherit from {@link BuiltInFunctionBodyExprNode}
 * on purpose, to illustrate the difference in adding this,
 * and {@link PowFunctionBodyExprNode}, to the global scope in
 * {@link com.endoflineblog.truffle.part_06.EasyScriptTruffleLanguage}.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/abs">Math.abs()</a>
 */
@NodeChild("argument")
public abstract class AbsFunctionBodyExprNode extends EasyScriptExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int intAbs(int argument) {
        // Integer.MIN_VALUE is too big to fit negated into an int
        return argument < 0 ? Math.negateExact(argument) : argument;
    }

    @Specialization(replaces = "intAbs")
    protected double doubleAbs(double argument) {
        return Math.abs(argument);
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
