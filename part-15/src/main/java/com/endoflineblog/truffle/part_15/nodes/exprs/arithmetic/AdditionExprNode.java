package com.endoflineblog.truffle.part_15.nodes.exprs.arithmetic;

import com.endoflineblog.truffle.part_15.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_15.nodes.exprs.BinaryOperationExprNode;
import com.endoflineblog.truffle.part_15.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_15.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The Node representing number addition.
 * Identical to the class with the same name from part 14.
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
     * A "fast" string concatenation specialization.
     */
    @Specialization
    protected TruffleString concatenateTruffleStrings(TruffleString leftValue, TruffleString rightValue,
            @Cached TruffleString.ConcatNode concatNode) {
        return EasyScriptTruffleStrings.concat(leftValue, rightValue, concatNode);
    }

    /**
     * The way addition works in JavaScript is that it turns into string concatenation
     * if either argument to it is a complex value.
     * Complex values are functions, arrays, strings, and objects (like Math).
     * Numbers and booleans are not complex values,
     * and nor is 'undefined' (and 'null', but EasyScript doesn't support that one yet).
     */
    @Specialization(guards = "isComplex(leftValue) || isComplex(rightValue)")
    protected TruffleString concatenateComplexAsStrings(Object leftValue, Object rightValue,
            @Cached TruffleString.FromJavaStringNode fromJavaStringNode) {
        return EasyScriptTruffleStrings.fromJavaString(
                EasyScriptTruffleStrings.concatToStrings(leftValue, rightValue),
                fromJavaStringNode);
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
    protected double addNonNumber(
            @SuppressWarnings("unused") Object leftValue,
            @SuppressWarnings("unused") Object rightValue) {
        return Double.NaN;
    }
}
