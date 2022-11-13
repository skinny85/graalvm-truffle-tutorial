package com.endoflineblog.truffle.part_10.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

@ExportLibrary(InteropLibrary.class)
public final class ArrayObject extends DynamicObject {
    private final Object[] arrayElements;

    public ArrayObject(Shape arrayShape, Object[] arrayElements) {
        super(arrayShape);
        this.arrayElements = arrayElements;
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize() {
        return this.arrayElements.length;
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        // in JavaScript, it's legal to take any index of an array -
        // indexes out of bounds simply return 'undefined'
        return true;
    }

    @ExportMessage
    Object readArrayElement(long index) {
        return index >= 0 && index < this.arrayElements.length
                ? this.arrayElements[(int) index]
                : Undefined.INSTANCE;
    }
}
