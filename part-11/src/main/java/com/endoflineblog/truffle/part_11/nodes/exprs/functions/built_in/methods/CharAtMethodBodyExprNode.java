package com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_11.runtime.StringObject;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class CharAtMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization
    protected StringObject charAtInt(StringObject stringObject, int index) {
        return stringObject.charAt(index);
    }

    @Specialization
    protected StringObject charAtNonInt(StringObject stringObject,
            @SuppressWarnings("unused") Object nonIntIndex) {
        return stringObject.charAt(0);
    }
}
