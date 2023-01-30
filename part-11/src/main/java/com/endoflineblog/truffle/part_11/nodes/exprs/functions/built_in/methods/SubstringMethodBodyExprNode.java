package com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_11.runtime.StringObject;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class SubstringMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization
    protected StringObject substringWithStartAndEnd(StringObject stringObject, int start, int end) {
        return stringObject.substring(start, end);
    }

    @Specialization
    protected StringObject substringWithJustStart(StringObject stringObject, int start,
            @SuppressWarnings("unused") Object end) {
        return stringObject.substring(start);
    }

    @Fallback
    protected Object substringWithoutArguments(Object stringObject,
            @SuppressWarnings("unused") Object start,
            @SuppressWarnings("unused") Object end) {
        return stringObject;
    }
}
