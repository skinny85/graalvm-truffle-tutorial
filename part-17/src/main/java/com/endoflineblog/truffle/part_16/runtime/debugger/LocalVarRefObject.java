package com.endoflineblog.truffle.part_16.runtime.debugger;

import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.source.SourceSection;

/**
 * An object that represents a reference to a local variable.
 */
public final class LocalVarRefObject extends RefObject {
    private final int localVarSlot;

    public LocalVarRefObject(String refName, SourceSection refSourceSection,
            int localVarSlot) {
        super(refName, refSourceSection);
        this.localVarSlot = localVarSlot;
    }

    @Override
    public Object readReference(Frame frame, DynamicObjectLibrary dynamicObjectLibrary) {
        Object result = frame.getValue(this.localVarSlot);
        // in some cases, the values of local variables might not be populated yet
        // (if we are on a statement before the declaration of the variable inside the block)
        return result == null ? Undefined.INSTANCE : result;
    }

    @Override
    public void writeReference(Frame frame, Object value, DynamicObjectLibrary dynamicObjectLibrary) {
        frame.setObject(this.localVarSlot, value);
    }
}
