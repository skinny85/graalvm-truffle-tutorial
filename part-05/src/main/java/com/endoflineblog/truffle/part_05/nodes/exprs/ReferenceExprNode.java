package com.endoflineblog.truffle.part_05.nodes.exprs;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "frameSlot", type = FrameSlot.class)
public abstract class ReferenceExprNode extends EasyScriptExprNode {
    protected abstract FrameSlot getFrameSlot();

    @Specialization(guards = "frame.isInt(getFrameSlot())")
    protected int readInt(VirtualFrame frame) {
        // We cannot just use frame.getInt(this.getFrameSlot()) here,
        // as that throws FrameSlotTypeException,
        // which is a "slow path", checked exception.
        // Instead, use the FrameUtil class that ships with Truffle.
        // It's safe because of the check guarding this specialization
        return FrameUtil.getIntSafe(frame, this.getFrameSlot());
    }

    @Specialization(guards = "frame.isDouble(getFrameSlot())", replaces = "readInt")
    protected double readDouble(VirtualFrame frame) {
        return FrameUtil.getDoubleSafe(frame, this.getFrameSlot());
    }
}
