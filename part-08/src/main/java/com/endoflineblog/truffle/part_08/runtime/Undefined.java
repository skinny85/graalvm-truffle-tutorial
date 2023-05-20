package com.endoflineblog.truffle.part_08.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * The class that represents the 'undefined' value in JavaScript.
 * Identical to the class with the same name from part 7.
 */
@ExportLibrary(InteropLibrary.class)
public final class Undefined implements TruffleObject {
    public static final Undefined INSTANCE = new Undefined();

    private Undefined() {
    }

    @ExportMessage
    boolean isNull() {
        return true;
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return this.toString();
    }

    @Override
    public String toString() {
        return "undefined";
    }
}
