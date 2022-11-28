package com.endoflineblog.truffle.part_10.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

@ExportLibrary(InteropLibrary.class)
public final class ArrayObject extends DynamicObject {
    private Object[] arrayElements;

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
        // In JavaScript, it's legal to take any index of an array -
        // indexes out of bounds simply return 'undefined'.
        // However, in GraalVM interop,
        // the same index cannot be both readable and insertable,
        // so we consider elements readable that are in the array
        return index >= 0 && index < this.arrayElements.length;
    }

    @ExportMessage
    Object readArrayElement(long index) {
        return this.isArrayElementReadable(index)
                ? this.arrayElements[(int) index]
                : Undefined.INSTANCE;
    }

    @ExportMessage
    boolean isArrayElementModifiable(long index) {
        return this.isArrayElementReadable(index);
    }

    @ExportMessage
    boolean isArrayElementInsertable(long index) {
        return index >= this.arrayElements.length;
    }

    @ExportMessage
    void writeArrayElement(long index, Object value) {
        if (this.isArrayElementModifiable(index)) {
            this.arrayElements[(int) index] = value;
        } else {
            Object[] newArrayElements = new Object[(int) index + 1];
            for (int i = 0; i < index; i++) {
                newArrayElements[i] = i < this.arrayElements.length
                        ? this.arrayElements[i]
                        : Undefined.INSTANCE;
            }
            newArrayElements[(int) index] = value;
            this.arrayElements = newArrayElements;
        }
    }
}