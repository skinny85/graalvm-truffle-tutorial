package com.endoflineblog.truffle.part_13.nodes.exprs.functions.built_in.methods;

import com.endoflineblog.truffle.part_13.nodes.exprs.functions.built_in.BuiltInFunctionBodyExprNode;
import com.endoflineblog.truffle.part_13.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * An expression Node that represents the implementation of the built-in
 * {@code charAt()} method of strings.
 * Identical to the class with the same name from part 12.
 */
public abstract class CharAtMethodBodyExprNode extends BuiltInFunctionBodyExprNode {
    /**
     * The specialization for when {@code charAt()}
     * is called with an integer argument.
     * In that case, we return a one-element substring of the target of the method call -
     * unless the index is out of bounds, in which case we return the empty string.
     */
    @Specialization
    protected TruffleString charAtInt(TruffleString self, int index,
            @Cached @Shared("lengthNode") TruffleString.CodePointLengthNode lengthNode,
            @Cached @Shared("substringNode") TruffleString.SubstringNode substringNode) {
        return index < 0 || index >= EasyScriptTruffleStrings.length(self, lengthNode)
            ? EasyScriptTruffleStrings.EMPTY
            : EasyScriptTruffleStrings.substring(self, index, 1, substringNode);
    }

    /**
     * The specialization for when {@code charAt()}
     * is called either without an argument,
     * or with one that is not an integer.
     * In that case, behave as if it was called with {@code 0}.
     */
    @Fallback
    protected TruffleString charAtNonInt(Object self,
            @SuppressWarnings("unused") Object nonIntIndex,
            @Cached @Shared("lengthNode") TruffleString.CodePointLengthNode lengthNode,
            @Cached @Shared("substringNode") TruffleString.SubstringNode substringNode) {
        // we know that 'self' is for sure a TruffleString
        // because of how reading string properties works in ReadTruffleStringPropertyNode,
        // but we need to declare it as Object here because of @Fallback
        return this.charAtInt((TruffleString) self, 0, lengthNode, substringNode);
    }
}
