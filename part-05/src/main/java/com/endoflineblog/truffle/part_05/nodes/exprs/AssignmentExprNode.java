package com.endoflineblog.truffle.part_05.nodes.exprs;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the expression of assigning a value to a variable in EasyScript.
 */
@NodeChild(value = "initializerExpr")
@NodeField(name = "frameSlot", type = FrameSlot.class)
public abstract class AssignmentExprNode extends EasyScriptExprNode {
    protected abstract FrameSlot getFrameSlot();

    @Specialization
    protected int intVariable(VirtualFrame frame, int value) {
        FrameSlot frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setFrameSlotKind(frameSlot, FrameSlotKind.Int);
        frame.setInt(frameSlot, value);
        return value;
    }

    @Specialization(replaces = "intVariable")
    protected double doubleVariable(VirtualFrame frame, double value) {
        FrameSlot frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setFrameSlotKind(frameSlot, FrameSlotKind.Double);
        frame.setDouble(frameSlot, value);
        return value;
    }
}
