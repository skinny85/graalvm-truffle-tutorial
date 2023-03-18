package com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_11.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

public abstract class CharAtMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    @Specialization
    protected TruffleString charAtInt(TruffleString self, int index,
            @Cached TruffleString.CodePointLengthNode lengthNode,
            @Cached TruffleString.SubstringNode substringNode) {
        return index < 0 || index >= EasyScriptTruffleStrings.length(self, lengthNode)
            ? EasyScriptTruffleStrings.EMPTY
            : EasyScriptTruffleStrings.substring(self, index, 1, substringNode);
    }

    @Specialization
    protected TruffleString charAtNonInt(TruffleString self,
            @SuppressWarnings("unused") Object nonIntIndex,
            @Cached TruffleString.SubstringNode substringNode) {
        return EasyScriptTruffleStrings.substring(self, 0, 1, substringNode);
    }
}
