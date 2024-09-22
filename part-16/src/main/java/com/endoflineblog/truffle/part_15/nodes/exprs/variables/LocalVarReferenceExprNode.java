package com.endoflineblog.truffle.part_15.nodes.exprs.variables;

import com.endoflineblog.truffle.part_15.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the reference to a variable local to a function.
 * Identical to the class with the same name from part 14.
 */
@NodeField(name = "frameSlot", type = int.class)
public abstract class LocalVarReferenceExprNode extends EasyScriptExprNode {
    protected abstract int getFrameSlot();

    @Specialization(guards = "frame.isInt(getFrameSlot())")
    protected int readInt(VirtualFrame frame) {
        return frame.getInt(this.getFrameSlot());
    }

    @Specialization(guards = "frame.isDouble(getFrameSlot())", replaces = "readInt")
    protected double readDouble(VirtualFrame frame) {
        return frame.getDouble(this.getFrameSlot());
    }

    @Specialization(guards = "frame.isBoolean(getFrameSlot())")
    protected boolean readBool(VirtualFrame frame) {
        return frame.getBoolean(this.getFrameSlot());
    }

    @Specialization(replaces = {"readInt", "readDouble", "readBool"})
    protected Object readObject(VirtualFrame frame) {
        return frame.getObject(this.getFrameSlot());
    }
}
