package com.endoflineblog.truffle.part_12.runtime;

import com.oracle.truffle.api.CallTarget;

/**
 * The object containing the {@code CallTarget}s
 * for the built-in methods of strings.
 * Identical to the class with the same name from part 11.
 */
public final class StringPrototype {
    /**
     * The {@link CallTarget} for the {@code charAt()} string method.
     *
     * @see com.endoflineblog.truffle.part_12.nodes.exprs.functions.built_in.methods.CharAtMethodBodyExprNode
     */
    public final CallTarget charAtMethod;

    public StringPrototype(CallTarget charAtMethod) {
        this.charAtMethod = charAtMethod;
    }
}
