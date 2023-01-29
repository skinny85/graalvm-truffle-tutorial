package com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_11.runtime.StringObject;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

@NodeField(name = "self", type = StringObject.class)
public abstract class SubstringMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    protected abstract StringObject getSelf();

    @Specialization
    protected StringObject substringWithStartAndEnd(int start, int end) {
        return this.getSelf().substring(start, end);
    }

    @Specialization
    protected StringObject substringWithJustStart(int start,
            @SuppressWarnings("unused") Object end) {
        return this.getSelf().substring(start);
    }

    @Fallback
    protected StringObject substringWithoutArguments(@SuppressWarnings("unused") Object start,
            @SuppressWarnings("unused") Object end) {
        return this.getSelf();
    }
}
