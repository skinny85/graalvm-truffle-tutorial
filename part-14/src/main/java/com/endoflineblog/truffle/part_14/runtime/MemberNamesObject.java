package com.endoflineblog.truffle.part_14.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * The class that implements a simple collection of member names of a {@link TruffleObject}.
 * Identical to the class with the same name from part 13.
 */
@ExportLibrary(InteropLibrary.class)
final class MemberNamesObject implements TruffleObject {
    private final Object[] names;

    MemberNamesObject(Object[] names) {
        this.names = names;
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize() {
        return this.names.length;
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0 && index < this.names.length;
    }

    @ExportMessage
    Object readArrayElement(long index) throws InvalidArrayIndexException {
        if (!this.isArrayElementReadable(index)) {
            throw InvalidArrayIndexException.create(index);
        }
        return this.names[(int) index];
    }
}
