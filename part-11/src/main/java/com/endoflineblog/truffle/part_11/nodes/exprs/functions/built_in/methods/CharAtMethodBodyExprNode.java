package com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class CharAtMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization
    protected TruffleString charAtInt(TruffleString self, int index,
            @Cached TruffleString.SubstringNode substringNode) {
        return substringNode.execute(self, index, 1, TruffleString.Encoding.UTF_16, true);
    }

    @Specialization
    protected TruffleString charAtNonInt(TruffleString self,
            @SuppressWarnings("unused") Object nonIntIndex,
            @Cached TruffleString.SubstringNode substringNode) {
        return this.charAtInt(self, 0, substringNode);
    }
}
