package com.endoflineblog.truffle.part_12.nodes.ops;

import com.endoflineblog.truffle.part_12.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class AdditionOrConcatenationOperationNode extends EasyScriptBinaryNumberOperationNode {
    @Specialization
    protected int addIntegers(int arg1, int arg2) {
        return Math.addExact(arg1, arg2);
    }

    @Specialization(replaces = "addIntegers")
    protected double addDoubles(double arg1, double arg2) {
        return arg1 + arg2;
    }

    @Specialization
    protected TruffleString concatenateTruffleStrings(TruffleString leftValue, TruffleString rightValue,
            @Cached TruffleString.ConcatNode concatNode) {
        return EasyScriptTruffleStrings.concat(leftValue, rightValue, concatNode);
    }

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

    @Fallback
    protected Object addNonNumbers(@SuppressWarnings("unused") Object arg1,
            @SuppressWarnings("unused") Object arg2) {
        return Double.NaN;
    }
}
