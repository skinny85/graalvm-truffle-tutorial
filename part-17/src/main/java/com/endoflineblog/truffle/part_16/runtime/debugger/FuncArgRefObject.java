package com.endoflineblog.truffle.part_16.runtime.debugger;

import com.oracle.truffle.api.frame.Frame;
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
    public Object readReference(Frame frame) {
        return frame.getArguments()[this.funcArgIndex];
    }

    @Override
    public void writeReference(Frame frame, Object value) {
        frame.getArguments()[this.funcArgIndex] = value;
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
