package com.endoflineblog.truffle.part_13.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * A {@link DynamicObject} that represents the prototype of a class.
 * That can be a user-defined class, or a built-in class,
 * like for functions, or arrays.
 * Identical to the class with the same name from part 12.
 */
@ExportLibrary(InteropLibrary.class)
public final class ClassPrototypeObject extends DynamicObject {
    private final String className;

    public ClassPrototypeObject(Shape shape, String className) {
        super(shape);

        this.className = className;
    }

    @Override
    public String toString() {
        return "[class " + this.className + "]";
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return this.toString();
    }
}
