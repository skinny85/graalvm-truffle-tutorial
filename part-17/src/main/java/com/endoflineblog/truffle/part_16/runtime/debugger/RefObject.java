package com.endoflineblog.truffle.part_16.runtime.debugger;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.source.SourceSection;

/**
 * This class represents a reference in EasyScript.
 * It's extended by {@link FuncArgRefObject} and {@link LocalVarRefObject}.
 */
@ExportLibrary(InteropLibrary.class)
public abstract class RefObject implements TruffleObject {
    public final String refName;
    private final SourceSection refSourceSection;

    public RefObject(String refName, SourceSection refSourceSection) {
        this.refName = refName;
        this.refSourceSection = refSourceSection;
    }

    public abstract Object readReference(Frame frame, DynamicObjectLibrary dynamicObjectLibrary);
    public abstract void writeReference(Frame frame, Object value, DynamicObjectLibrary dynamicObjectLibrary);

    @ExportMessage
    boolean isString() {
        return true;
    }

    @ExportMessage
    String asString() {
        return this.refName;
    }

    @ExportMessage
    boolean hasSourceLocation() {
        return this.refSourceSection != null;
    }

    @ExportMessage
    SourceSection getSourceLocation() {
        return this.refSourceSection;
    }
}
