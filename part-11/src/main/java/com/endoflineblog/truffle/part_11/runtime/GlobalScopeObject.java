package com.endoflineblog.truffle.part_11.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * This is the Truffle interop object that represents the global-level scope
 * that contains all global variables.
 * Identical to the class with the same name from part 16.
 */
@ExportLibrary(InteropLibrary.class)
public final class GlobalScopeObject extends DynamicObject {
    public GlobalScopeObject(Shape shape) {
        super(shape);
    }

    @ExportMessage
    boolean isScope() {
        return true;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member,
            @Cached @Shared DynamicObject.ContainsKeyNode containsKeyNode) {
        return containsKeyNode.execute(this, member);
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
            @Cached DynamicObject.GetKeyArrayNode getKeyArrayNode) {
        return new MemberNamesObject(getKeyArrayNode.execute(this));
    }

    @ExportMessage
    Object readMember(String member,
            @Cached DynamicObject.GetNode getNode) throws UnknownIdentifierException {
        Object value = getNode.execute(this, member, null);
        if (null == value) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
    }

    @ExportMessage
    boolean isMemberModifiable(String member,
            @Cached @Shared DynamicObject.ContainsKeyNode containsKeyNode) {
        return containsKeyNode.execute(this, member);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
            @Cached @Shared DynamicObject.ContainsKeyNode containsKeyNode) {
        return !containsKeyNode.execute(this, member);
    }

    @ExportMessage
    void writeMember(String member, Object value,
            @Cached DynamicObject.PutNode putNode) {
        putNode.execute(this, member, value);
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return "global";
    }

    @ExportMessage
    boolean hasLanguageId() {
        return true;
    }

    @ExportMessage
    String getLanguageId() {
        return "ezs";
    }
}
