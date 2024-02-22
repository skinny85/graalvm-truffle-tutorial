package com.endoflineblog.truffle.part_13.nodes.exprs.properties;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;

/**
 * A Node for writing a property of a JavaScript object.
 * Used by {@link PropertyWriteExprNode} and {@link com.endoflineblog.truffle.part_13.nodes.exprs.arrays.ArrayIndexWriteExprNode}.
 */
public abstract class CommonWritePropertyNode extends Node {
    public abstract Object executeWriteProperty(Object target, Object property, Object rvalue);

    @Specialization(guards = "interopLibrary.isMemberWritable(target, propertyName)", limit = "2")
    protected Object writeProperty(Object target, String propertyName, Object rvalue,
            @CachedLibrary("target") InteropLibrary interopLibrary) {
        try {
            interopLibrary.writeMember(target, propertyName, rvalue);
        } catch (UnsupportedMessageException | UnsupportedTypeException | UnknownIdentifierException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
        return rvalue;
    }

    /**
     * Attempting to write any property of {@code undefined}
     * results in an error in JavaScript.
     */
    @Specialization(guards = "interopLibrary.isNull(target)", limit = "2")
    protected Object writePropertyOfUndefined(@SuppressWarnings("unused") Object target,
            Object property, Object rvalue,
            @CachedLibrary("target") @SuppressWarnings("unused") InteropLibrary interopLibrary) {
        throw new EasyScriptException("Cannot set properties of undefined (setting '" + property + "')");
    }

    /**
     * Writing a property of anything that is not {@code undefined}
     * but doesn't have any members simply returns the right-hand side of the assignment.
     */
    @Fallback
    protected Object writePropertyOfNonUndefinedWithoutMembers(@SuppressWarnings("unused") Object target,
            @SuppressWarnings("unused") Object property, Object rvalue) {
        return rvalue;
    }
}
