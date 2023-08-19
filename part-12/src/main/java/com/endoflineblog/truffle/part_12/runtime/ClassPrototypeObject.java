package com.endoflineblog.truffle.part_12.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

@ExportLibrary(InteropLibrary.class)
public final class ClassPrototypeObject extends DynamicObject {
    public final String className;

    public ClassPrototypeObject(Shape shape, String className) {
        super(shape);

        this.className = className;
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return "[class " + this.className + "]";
    }
}
