package com.endoflineblog.truffle.part_16.runtime.debugger;

import com.endoflineblog.truffle.part_16.runtime.Environment;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.source.SourceSection;

public final class LocalFuncVarRefObject extends RefObject {
    private final int localVarSlot;

    public LocalFuncVarRefObject(String refName, SourceSection refSourceSection,
            int localVarSlot) {
        super(refName, refSourceSection);
        this.localVarSlot = localVarSlot;
    }

    @Override
    public Object readReference(Frame frame, DynamicObjectLibrary dynamicObjectLibrary) {
        Environment environment = (Environment) frame.getArguments()[1];
        // in some cases, the values of local variables might not be populated yet
        // (if we are on a statement before the declaration of the variable inside the block),
        // so make sure to return 'undefined' in the default case
        return dynamicObjectLibrary.getOrDefault(environment, this.localVarSlot, Undefined.INSTANCE);
    }

    @Override
    public void writeReference(Frame frame, Object value, DynamicObjectLibrary dynamicObjectLibrary) {
        Environment environment = (Environment) frame.getArguments()[1];
        dynamicObjectLibrary.put(environment, this.localVarSlot, value);
    }
}
