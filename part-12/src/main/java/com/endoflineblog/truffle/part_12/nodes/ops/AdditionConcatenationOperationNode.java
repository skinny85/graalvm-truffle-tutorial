package com.endoflineblog.truffle.part_12.nodes.ops;

import com.endoflineblog.truffle.part_12.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class AdditionConcatenationOperationNode extends EasyScriptBinaryOperationNode {
    @Specialization
    protected int addIntegers(int lvalue, int rvalue) {
        return Math.addExact(lvalue, rvalue);
    }

    @Specialization(replaces = "addIntegers")
    protected double addDoubles(double lvalue, double rvalue) {
        return lvalue + rvalue;
    }

    @Specialization
    protected TruffleString concatenateTruffleStrings(TruffleString lvalue, TruffleString rvalue,
            @Cached TruffleString.ConcatNode concatNode) {
        return EasyScriptTruffleStrings.concat(lvalue, rvalue, concatNode);
    }

    @Specialization(guards = "isComplex(lvalue) || isComplex(rvalue)")
    protected TruffleString concatenateComplexAsStrings(Object lvalue, Object rvalue,
            @Cached TruffleString.FromJavaStringNode fromJavaStringNode) {
        return EasyScriptTruffleStrings.fromJavaString(
                EasyScriptTruffleStrings.concatToStrings(lvalue, rvalue),
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
    protected Object addNonNumbers(@SuppressWarnings("unused") Object lvalue,
            @SuppressWarnings("unused") Object rvalue) {
        return Double.NaN;
    }
}
