package com.endoflineblog.truffle.part_16.runtime;

import com.endoflineblog.truffle.part_16.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_16.EasyScriptTruffleLanguage;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

/**
 * A Truffle {@link DynamicObject} that implements integer-indexed JavaScript arrays.
 * Identical to the class with the same name from part 15.
 */
@ExportLibrary(InteropLibrary.class)
public final class ArrayObject extends JavaScriptObject {
    // must be package-private, since it's used in specialization guard expressions
    static final String LENGTH_PROP = "length";

    /**
     * The field that signifies this {@link DynamicObject}
     * always has a property called {@code length}.
     * Used in the array shape created in the
     * {@link EasyScriptTruffleLanguage TruffleLanguage class for this chapter}.
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
        if (!this.isArrayElementModifiable(index)) {
            // in JavaScript, it's legal to write past the array size
            this.resetArray(index + 1, objectLibrary);
        }
        this.arrayElements[(int) index] = value;
    }

    /**
     * The {@link InteropLibrary#writeMember}
     * exported as a class, instead of a method.
     * This allows using specializations for implementing the message.
     */
    @ExportMessage
    static class WriteMember {
        @Specialization(guards = {"LENGTH_PROP.equals(member)", "length >= 0"})
        static void writeNonNegativeIntLength(ArrayObject arrayObject,
                @SuppressWarnings("unused") String member, int length,
                @CachedLibrary("arrayObject") DynamicObjectLibrary dynamicObjectLibrary) {
            arrayObject.resetArray(length, dynamicObjectLibrary);
        }

        @Specialization(guards = "LENGTH_PROP.equals(member)")
        static void writeNegativeOrNonIntLength(
                @SuppressWarnings("unused") ArrayObject arrayObject,
                @SuppressWarnings("unused") String member,
                Object length) {
            throw new EasyScriptException("Invalid array length: " + length);
        }

        @Fallback
        static void writeNonLength(ArrayObject arrayObject, String member, Object value,
                @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary) {
            arrayObject.writeMember(member, value, dynamicObjectLibrary);
        }
    }

    private void resetArray(long length, DynamicObjectLibrary objectLibrary) {
        Object[] newArrayElements = new Object[(int) length];
        for (int i = 0; i < length; i++) {
            newArrayElements[i] = i < this.arrayElements.length
                    ? this.arrayElements[i]
                    : Undefined.INSTANCE;
        }
        this.setArrayElements(newArrayElements, objectLibrary);
    }

    private void setArrayElements(Object[] arrayElements, DynamicObjectLibrary objectLibrary) {
        this.arrayElements = arrayElements;
        this.writeMember(LENGTH_PROP, arrayElements.length, objectLibrary);
    }
}
