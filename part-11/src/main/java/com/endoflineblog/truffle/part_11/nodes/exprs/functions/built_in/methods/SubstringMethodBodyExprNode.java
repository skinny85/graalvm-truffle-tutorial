package com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class SubstringMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization
    protected TruffleString substringWithStartAndEnd(TruffleString self,
            int start, int end,
            @Cached TruffleString.SubstringNode substringNode) {
        return substringNode.execute(self, start, end - start, TruffleString.Encoding.UTF_16, true);
    }

    @Specialization
    protected TruffleString substringWithJustStart(TruffleString self,
            int start, @SuppressWarnings("unused") Object end,
            @Cached TruffleString.SubstringNode substringNode,
            @Cached TruffleString.CodePointLengthNode lengthNode) {
        return this.substringWithStartAndEnd(self, start,
                lengthNode.execute(self, TruffleString.Encoding.UTF_16),
                substringNode);
    }

    @Fallback
    protected Object substringWithoutArguments(Object self,
            @SuppressWarnings("unused") Object start,
            @SuppressWarnings("unused") Object end) {
        return self;
    }
}
