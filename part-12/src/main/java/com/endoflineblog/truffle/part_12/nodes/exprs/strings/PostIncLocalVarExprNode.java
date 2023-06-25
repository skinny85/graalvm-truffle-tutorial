package com.endoflineblog.truffle.part_12.nodes.exprs.strings;

import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "frameSlot", type = int.class)
@ImportStatic(FrameSlotKind.class)
public abstract class PostIncLocalVarExprNode extends EasyScriptExprNode {
    protected abstract int getFrameSlot();

    @Specialization(guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Int",
            rewriteOn = ArithmeticException.class)
    protected int intIncrement(VirtualFrame frame) {
        int frameSlot = this.getFrameSlot();
        int prevValue = frame.getInt(frameSlot);
        int newValue = Math.addExact(prevValue, 1);
        frame.setInt(frameSlot, newValue); // we know the kind of the slot is Int
        return prevValue;
    }

    @Specialization(replaces = "intIncrement",
            guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Double")
    protected double doubleIncrement(VirtualFrame frame) {
        int frameSlot = this.getFrameSlot();
        double prevValue = frame.getDouble(frameSlot);
        double newValue = prevValue + 1;
        frame.setDouble(frameSlot, newValue); // we know the kind of the slot is Double
        return prevValue;
    }

    @Specialization
    protected double nonNumberIncrement(VirtualFrame frame) {
        int frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Object);
        frame.setObject(frameSlot, Double.NaN);
        return Double.NaN;
    }
}
