package com.endoflineblog.truffle.part_13.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

@ExportLibrary(InteropLibrary.class)
public abstract class InteropDynamicObject extends DynamicObject {
    public InteropDynamicObject(Shape shape) {
        super(shape);
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
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
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary) {
        return new MemberNamesObject(thisObjectLibrary.getKeyArray(this));
    }
}
