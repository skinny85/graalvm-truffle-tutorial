package com.endoflineblog.truffle.part_14.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

/**
 * The class representing the prototype of the {@code Object}
 * class in JavaScript.
 * This is a unique prototype,
 * as it's the only one in the language that doesn't have a parent class prototype
 * (as {@code Object} is the root of the class hierarchy).
 * Because of that, we re-implement the logic of property reads to not reference the parent prototype
 * (which we pass as an anonymous subclass of {@link DynamicObject}
 * to {@link ClassPrototypeObject}).
 */
@ExportLibrary(InteropLibrary.class)
public final class ObjectPrototype extends ClassPrototypeObject {
    public ObjectPrototype(Shape shape) {
        super(shape, "Object", new DynamicObject(shape) {});
    }

    @ExportMessage
    boolean isMemberReadable(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary) {
        return thisObjectLibrary.containsKey(this, member);
    }

    @ExportMessage
    Object readMember(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary)
            throws UnknownIdentifierException {
        Object value = thisObjectLibrary.getOrDefault(this, member, null);
        if (value == null) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
    }

    @ExportMessage
    boolean isMemberModifiable(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary) {
        return this.isMemberReadable(member, thisObjectLibrary);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary) {
        return !this.isMemberModifiable(member, thisObjectLibrary);
    }
}
