package com.endoflineblog.truffle.part_13.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

/**
 * A Truffle {@link DynamicObject} that implements integer-indexed JavaScript arrays.
 * Identical to the class with the same name from part 11.
 */
@ExportLibrary(InteropLibrary.class)
public final class ArrayObject extends JavaScriptObject {
    /**
     * The field that signifies this {@link DynamicObject}
     * always has a property called {@code length}.
     * Used in the array shape created in the
     * {@link com.endoflineblog.truffle.part_13.EasyScriptTruffleLanguage TruffleLanguage class for this chapter}.
     */
    @DynamicField
    private long length;

    private Object[] arrayElements;

    public ArrayObject(Shape arrayShape, ClassPrototypeObject arrayPrototype, Object[] arrayElements) {
        super(arrayShape, arrayPrototype);
        this.setArrayElements(arrayElements, DynamicObjectLibrary.getUncached());
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
    void writeArrayElement(long index, Object value,
            @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        if (this.isArrayElementModifiable(index)) {
            this.arrayElements[(int) index] = value;
        } else {
            // in JavaScript, it's legal to write past the array size
            Object[] newArrayElements = new Object[(int) index + 1];
            for (int i = 0; i < index; i++) {
                newArrayElements[i] = i < this.arrayElements.length
                        ? this.arrayElements[i]
                        : Undefined.INSTANCE;
            }
            newArrayElements[(int) index] = value;
            this.setArrayElements(newArrayElements, objectLibrary);
        }
    }

    private void setArrayElements(Object[] arrayElements, DynamicObjectLibrary objectLibrary) {
        this.arrayElements = arrayElements;
        objectLibrary.putInt(this, "length", arrayElements.length);
    }
}
