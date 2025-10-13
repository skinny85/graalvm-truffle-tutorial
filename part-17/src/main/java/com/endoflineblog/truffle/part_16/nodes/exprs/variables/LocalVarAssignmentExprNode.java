package com.endoflineblog.truffle.part_16.nodes.exprs.variables;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the assignment to a variable local to a function.
 * Very similar to the class with the same name from part 14,
 * the only difference is we save the name of the variable this represents a reference to,
 * and we use it in {@link com.endoflineblog.truffle.part_16.nodes.stmts.blocks.LocalVarNodeVisitor}.
 */
@NodeChild("initializerExpr")
@NodeField(name = "slotName", type = String.class)
@NodeField(name = "frameSlot", type = int.class)
@ImportStatic(FrameSlotKind.class)
public abstract class LocalVarAssignmentExprNode extends EasyScriptExprNode {
    /**
     * We reference this field in {@link com.endoflineblog.truffle.part_16.nodes.stmts.blocks.LocalVarNodeVisitor},
     * so we need to make it {@code public}.
     */
    public abstract String getSlotName();

    /**
     * This method is now used in {@link com.endoflineblog.truffle.part_16.nodes.stmts.blocks.LocalVarNodeVisitor},
     * so we change it to be {@code public}.
     */
    public abstract int getFrameSlot();

    @Specialization(guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Int")
    protected int intAssignment(VirtualFrame frame, int value) {
        int frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Int);
        frame.setInt(frameSlot, value);
        return value;
    }

    @Specialization(replaces = "intAssignment",
            guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
                    "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Double")
    protected double doubleAssignment(VirtualFrame frame, double value) {
        int frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Double);
        frame.setDouble(frameSlot, value);
        return value;
    }

    @Specialization(guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Boolean")
    protected boolean boolAssignment(VirtualFrame frame, boolean value) {
        int frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Boolean);
        frame.setBoolean(frameSlot, value);
        return value;
    }

    @Specialization(replaces = {"intAssignment", "doubleAssignment", "boolAssignment"})
    protected Object objectAssignment(VirtualFrame frame, Object value) {
        int frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Object);
        frame.setObject(frameSlot, value);
        return value;
    }
}
