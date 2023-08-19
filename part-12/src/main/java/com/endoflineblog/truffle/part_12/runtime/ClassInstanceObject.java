package com.endoflineblog.truffle.part_12.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

@ExportLibrary(InteropLibrary.class)
public final class ClassInstanceObject implements TruffleObject {
    private final ClassPrototypeObject classPrototypeObject;

    public ClassInstanceObject(ClassPrototypeObject classPrototypeObject) {
        this.classPrototypeObject = classPrototypeObject;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new MemberNamesObject(new Object[]{});
    }
}
