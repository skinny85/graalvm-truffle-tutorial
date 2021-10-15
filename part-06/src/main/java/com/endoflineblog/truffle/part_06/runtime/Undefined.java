package com.endoflineblog.truffle.part_06.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * The class that represents the 'undefined' value in JavaScript.
 * It's a singleton.
 */
@ExportLibrary(InteropLibrary.class)
public final class Undefined implements TruffleObject {
    public static final Undefined INSTANCE = new Undefined();

    private Undefined() {
    }

    @Override
    public String toString() {
        return "Undefined";
    }

    @ExportMessage
    boolean isNull() {
        return true;
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return "undefined";
    }
}
