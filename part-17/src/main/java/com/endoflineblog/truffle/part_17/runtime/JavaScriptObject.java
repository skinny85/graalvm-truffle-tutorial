package com.endoflineblog.truffle.part_17.runtime;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * A {@link DynamicObject} that is the base class of all objects in EasyScript,
 * including user-defined class instances, built-in objects like arrays and functions,
 * and class prototypes.
 * Identical to the class with the same name from part 16.
 */
@ExportLibrary(InteropLibrary.class)
public class JavaScriptObject extends DynamicObject {
    public final DynamicObject prototype;

    public JavaScriptObject(Shape shape, DynamicObject prototype) {
        super(shape);

        this.prototype = prototype;
    }

    @Override
    public String toString() {
        return "[object Object]";
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return this.toString();
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member,
            @Cached @Shared DynamicObject.ContainsKeyNode containsKeyNode,
            @CachedLibrary("this.prototype") InteropLibrary prototypeInteropLibrary) {
        return containsKeyNode.execute(this, member) ||
                prototypeInteropLibrary.isMemberReadable(this.prototype, member);
    }

    @ExportMessage
    Object readMember(String member,
            @Cached DynamicObject.GetNode getNode,
            @CachedLibrary("this.prototype") InteropLibrary prototypeInteropLibrary)
            throws UnknownIdentifierException, UnsupportedMessageException {
        Object value = getNode.execute(this, member, null);
        if (value == null) {
            return prototypeInteropLibrary.readMember(this.prototype, member);
        }
        return value;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
            @Cached DynamicObject.GetKeyArrayNode getKeyArrayNode) {
        return new MemberNamesObject(getKeyArrayNode.execute(this));
    }

    @ExportMessage
    boolean isMemberModifiable(String member,
            @Cached @Shared DynamicObject.ContainsKeyNode containsKeyNode,
            @CachedLibrary("this.prototype") InteropLibrary prototypeInteropLibrary) {
        return this.isMemberReadable(member, containsKeyNode, prototypeInteropLibrary);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
            @Cached @Shared DynamicObject.ContainsKeyNode containsKeyNode,
            @CachedLibrary("this.prototype") InteropLibrary prototypeInteropLibrary) {
        return !this.isMemberModifiable(member, containsKeyNode, prototypeInteropLibrary);
    }

    @ExportMessage
    void writeMember(String member, Object value,
            @Cached DynamicObject.PutNode putNode) {
        putNode.execute(this, member, value);
    }
}
