package com.endoflineblog.truffle.part_16.runtime.debugger;

import com.endoflineblog.truffle.part_16.runtime.Environment;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.source.SourceSection;

import java.util.Objects;

/**
 * An object that represents a reference to a function argument.
 */
public final class FuncArgRefObject extends RefObject {
    private final int funcArgIndex;

    public FuncArgRefObject(String refName, SourceSection refSourceSection,
            int funcArgIndex) {
        super(refName, refSourceSection);
        this.funcArgIndex = funcArgIndex;
    }

    @Override
    public Object readReference(Frame frame, DynamicObjectLibrary dynamicObjectLibrary) {
        if (this.funcArgIndex == 0) {
            return frame.getArguments()[0];
        }
        Environment environment = (Environment) frame.getArguments()[1];
        return dynamicObjectLibrary.getOrDefault(environment, this.funcArgIndex, null);
    }

    @Override
    public void writeReference(Frame frame, Object value, DynamicObjectLibrary dynamicObjectLibrary) {
        // ToDO: should we allow writing to the 'this' (index 0)?
        Environment environment = (Environment) frame.getArguments()[1];
        dynamicObjectLibrary.put(environment, this.funcArgIndex, value);
    }

    /**
     * We use this class in a {@link java.util.Set},
     * so we need to override {@code #equals()} and {@link #hashCode()}.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FuncArgRefObject)) {
            return false;
        }
        FuncArgRefObject that = (FuncArgRefObject) other;
        return this.funcArgIndex == that.funcArgIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.funcArgIndex);
    }
}
