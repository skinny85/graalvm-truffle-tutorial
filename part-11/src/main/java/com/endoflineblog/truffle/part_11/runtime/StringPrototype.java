package com.endoflineblog.truffle.part_11.runtime;

import com.oracle.truffle.api.CallTarget;

/**
 * The object containing the {@code CallTarget}s
 * for the built-in methods of strings.
 * Used in the expression Node for reading {@code TruffleString} properties.
 * Nodes access it through the {@code TruffleLanguageContext}'s {@code stringPrototype} field.
 *
 * @see com.endoflineblog.truffle.part_11.nodes.exprs.strings.ReadTruffleStringPropertyExprNode
 * @see com.endoflineblog.truffle.part_11.EasyScriptLanguageContext#stringPrototype
 */
public final class StringPrototype {
    /**
     * The {@link CallTarget} for the {@code charAt()} string method.
     *
     * @see com.endoflineblog.truffle.part_11.nodes.exprs.functions.built_in.methods.CharAtMethodBodyExprNode
     */
    public final CallTarget charAtMethod;

    public StringPrototype(CallTarget charAtMethod) {
        this.charAtMethod = charAtMethod;
    }
}
