package com.endoflineblog.truffle.part_07.nodes.exprs;

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the assignment to a variable local to a function.
 */
@NodeChild("initializerExpr")
@NodeField(name = "frameSlot", type = int.class)
@ImportStatic(FrameSlotKind.class)
public abstract class LocalVarAssignmentExprNode extends EasyScriptExprNode {
    protected abstract int getFrameSlot();

    @Specialization(guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Int")
    protected int intAssignment(VirtualFrame frame, int value) {
        var frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Int);
        frame.setInt(frameSlot, value);
        return value;
    }

    @Specialization(replaces = "intAssignment",
            guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
                    "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Double")
    protected double doubleAssignment(VirtualFrame frame, double value) {
        var frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Double);
        frame.setDouble(frameSlot, value);
        return value;
    }

    @Specialization(replaces = {"intAssignment", "doubleAssignment"})
    protected Object objectAssignment(VirtualFrame frame, Object value) {
        var frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Object);
        frame.setObject(frameSlot, value);
        return value;
    }
}
