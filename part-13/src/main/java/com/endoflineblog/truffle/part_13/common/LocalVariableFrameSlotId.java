package com.endoflineblog.truffle.part_13.common;

import java.util.Objects;

/**
 * A class that represents the full identifier of a local variable used for a frame slot.
 * Identical to the class with the same name from part 11.
 */
public final class LocalVariableFrameSlotId {
    public final String variableName;
    public final int index;

    public LocalVariableFrameSlotId(String variableName, int index) {
        this.variableName = variableName;
        this.index = index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableName, index);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LocalVariableFrameSlotId)) {
            return false;
        }
        var that = (LocalVariableFrameSlotId) other;
        return this.index == that.index &&
                this.variableName.equals(that.variableName);
    }

    @Override
    public String toString() {
        return this.variableName + "-" + this.index;
    }
}
