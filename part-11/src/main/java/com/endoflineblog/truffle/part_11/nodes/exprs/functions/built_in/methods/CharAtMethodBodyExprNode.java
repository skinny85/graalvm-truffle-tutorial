package com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_11.runtime.StringObject;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

@NodeField(name = "self", type = StringObject.class)
public abstract class CharAtMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    protected abstract StringObject getSelf();

    @Specialization
    protected StringObject charAtInt(int index) {
        return this.getSelf().charAt(index);
    }

    @Fallback
    protected StringObject charAtNonInt(@SuppressWarnings("unused") Object nonIntIndex) {
        return this.getSelf().charAt(0);
    }
}
