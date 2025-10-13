package com.endoflineblog.truffle.part_16.runtime.debugger;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * A simple {@link TruffleObject} that implements the array messages from
 * {@link InteropLibrary}. It represents an array of
 * {@link RefObject debugger references}.
 */
@ExportLibrary(InteropLibrary.class)
final class RefObjectsArray implements TruffleObject {
    @CompilationFinal(dimensions = 1)
    private final RefObject[] references;

    RefObjectsArray(RefObject[] references) {
        this.references = references;
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize() {
        return this.references.length;
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0 && index < this.references.length;
    }

    @ExportMessage
    Object readArrayElement(long index) throws InvalidArrayIndexException {
        if (this.isArrayElementReadable(index)) {
            return this.references[(int) index];
        } else {
            throw InvalidArrayIndexException.create(index);
        }
    }
}
