package com.endoflineblog.truffle.part_13.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

@ExportLibrary(InteropLibrary.class)
public final class ClassPrototypeChainObject extends ClassPrototypeObject {
    public final ClassPrototypeObject superClassPrototype;

    public ClassPrototypeChainObject(Shape shape, String className,
            ClassPrototypeObject superClassPrototype) {
        super(shape, className);

        this.superClassPrototype = superClassPrototype;
    }

    @ExportMessage
    Object readMember(String member,
            @CachedLibrary("this") DynamicObjectLibrary thisObjectLibrary,
            @CachedLibrary("this.superClassPrototype") InteropLibrary superClassInteropLibrary)
            throws UnknownIdentifierException, UnsupportedMessageException {
        Object value = thisObjectLibrary.getOrDefault(this, member, null);
        if (value == null) {
            return superClassInteropLibrary.readMember(this.superClassPrototype, member);
        }
        return value;
    }
}
