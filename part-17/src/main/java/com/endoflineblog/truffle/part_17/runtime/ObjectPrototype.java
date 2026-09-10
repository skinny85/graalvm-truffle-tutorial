package com.endoflineblog.truffle.part_17.runtime;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * The class representing the prototype of the {@code Object}
 * class in JavaScript.
 * Identical to the class with the same name from part 16.
 */
@ExportLibrary(InteropLibrary.class)
public final class ObjectPrototype extends ClassPrototypeObject {
    public ObjectPrototype(Shape shape) {
        super(shape, "Object", new DynamicObject(shape) {});
    }

    @ExportMessage
    boolean isMemberReadable(String member,
            @Cached @Shared DynamicObject.ContainsKeyNode containsKeyNode) {
        return containsKeyNode.execute(this, member);
    }

    @ExportMessage
    Object readMember(String member,
            @Cached DynamicObject.GetNode getNode)
            throws UnknownIdentifierException {
        Object value = getNode.execute(this, member, null);
        if (value == null) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
    }

    @ExportMessage
    boolean isMemberModifiable(String member,
            @Cached @Shared DynamicObject.ContainsKeyNode containsKeyNode) {
        return this.isMemberReadable(member, containsKeyNode);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
            @Cached @Shared DynamicObject.ContainsKeyNode containsKeyNode) {
        return !this.isMemberModifiable(member, containsKeyNode);
    }
}
