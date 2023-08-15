package com.endoflineblog.truffle.part_12.runtime;

import com.endoflineblog.truffle.part_12.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.strings.ReadTruffleStringPropertyExprNode;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

/**
 * A Truffle {@link DynamicObject} that implements integer-indexed JavaScript arrays.
 * Identical to the class with the same name from part 10.
 */
@ExportLibrary(InteropLibrary.class)
public final class ArrayObject extends JavaScriptObject {
    /**
     * The field that signifies this {@link DynamicObject}
     * always has a property called {@code length}.
     * Used in the array shape created in the
     * {@link EasyScriptTruffleLanguage TruffleLanguage class for this chapter}.
     */
    @DynamicField
    private long length;

    private Object[] arrayElements;

    public ArrayObject(Shape arrayShape, Object[] arrayElements) {
        super(arrayShape);
        this.setArrayElements(arrayElements, DynamicObjectLibrary.getUncached());
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize(@CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        try {
            return objectLibrary.getIntOrDefault(this, ReadTruffleStringPropertyExprNode.LENGTH_PROP, 0);
        } catch (UnexpectedResultException e) {
            throw new EasyScriptException(e.getMessage());
        }
    }

    @ExportMessage
    boolean isArrayElementReadable(long index,
            @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        // In JavaScript, it's legal to take any index of an array -
        // indexes out of bounds simply return 'undefined'.
        // However, in GraalVM interop,
        // the same index cannot be both readable and insertable,
        // so we consider elements readable that are in the array
        return index >= 0 && index < this.getArraySize(objectLibrary);
    }

    @ExportMessage
    Object readArrayElement(long index,
            @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return this.isIndexInArray(index, objectLibrary)
                ? this.arrayElements[(int) index]
                : Undefined.INSTANCE;
    }

    @ExportMessage
    boolean isArrayElementModifiable(long index,
            @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return this.isArrayElementReadable(index, objectLibrary);
    }

    @ExportMessage
    boolean isArrayElementInsertable(long index, @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return index >= this.getArraySize(objectLibrary);
    }

    @ExportMessage
    void writeArrayElement(long index, Object value,
            @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        if (this.isIndexInArray(index, objectLibrary)) {
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

    private boolean isIndexInArray(long index, DynamicObjectLibrary objectLibrary) {
        return this.isArrayElementReadable(index, objectLibrary) && index < this.arrayElements.length;
    }

    private void setArrayElements(Object[] arrayElements, DynamicObjectLibrary objectLibrary) {
        this.arrayElements = arrayElements;
        objectLibrary.putInt(this, ReadTruffleStringPropertyExprNode.LENGTH_PROP, arrayElements.length);
    }
}
