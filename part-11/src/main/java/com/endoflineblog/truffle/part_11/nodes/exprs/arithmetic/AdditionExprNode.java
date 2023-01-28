package com.endoflineblog.truffle.part_11.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_11.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_11.nodes.exprs.BinaryOperationExprNode;
import com.endoflineblog.truffle.part_11.runtime.StringObject;
import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The Node representing number addition.
 * Identical to the class with the same name from part 9.
 */
public abstract class AdditionExprNode extends BinaryOperationExprNode {
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int addInts(int leftValue, int rightValue) {
        return Math.addExact(leftValue, rightValue);
    }

    @Specialization(replaces = "addInts")
    protected double addDoubles(double leftValue, double rightValue) {
        return leftValue + rightValue;
    }

    /**
     * The way addition works in JavaScript is that it turns into string concatenation
     * if either argument to it is a complex value.
     * Complex values are functions, arrays, strings, and objects (like Math).
     * Numbers and booleans are not complex values,
     * and nor is 'undefined' (and 'null', but EasyScript doesn't support that one yet).
     */
    @Specialization(guards = "isComplex(leftValue) || isComplex(rightValue)")
    @TruffleBoundary
    protected StringObject concatenateComplexAsStrings(Object leftValue, Object rightValue) {
        return new StringObject(leftValue.toString() + rightValue.toString(), this.currentTruffleLanguage());
    }

    protected static boolean isComplex(Object value) {
        return !isPrimitive(value);
    }

    private static boolean isPrimitive(Object value) {
        return EasyScriptTypeSystemGen.isImplicitDouble(value) ||
                EasyScriptTypeSystemGen.isBoolean(value) ||
                value == Undefined.INSTANCE;
    }

    /**
     * If we get to this specialization, that means neither argument is complex,
     * but they are also not both numbers - meaning,
     * at least one of them is a boolean or {@code undefined}.
     * In this case, always return NaN.
     */
    @Fallback
    protected double addNonNumber(Object leftValue, Object rightValue) {
        return Double.NaN;
    }
}
