package com.endoflineblog.truffle.part_16.nodes.exprs.variables;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.nodes.exprs.frame.AbstractFrameGetNode;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlotKind;

/**
 * A Node that represents the assignment to a variable local to a function.
 * Very similar to the class with the same name from part 16,
 * the only difference is the {@link AbstractFrameGetNode}
 * that gets used for local variables in closures.
 */
@NodeChild(value = "currentOrParentFrameGetNode", type = AbstractFrameGetNode.class)
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

    @Specialization(guards = "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Int")
    protected int intAssignment(Frame currentOrParentFrame, int value) {
        int frameSlot = this.getFrameSlot();
        currentOrParentFrame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Int);
        currentOrParentFrame.setInt(frameSlot, value);
        return value;
    }

    @Specialization(replaces = "intAssignment",
            guards = "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
                    "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Double")
    protected double doubleAssignment(Frame currentOrParentFrame, double value) {
        int frameSlot = this.getFrameSlot();
        currentOrParentFrame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Double);
        currentOrParentFrame.setDouble(frameSlot, value);
        return value;
    }

    @Specialization(guards = "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
            "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Boolean")
    protected boolean boolAssignment(Frame currentOrParentFrame, boolean value) {
        int frameSlot = this.getFrameSlot();
        currentOrParentFrame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Boolean);
        currentOrParentFrame.setBoolean(frameSlot, value);
        return value;
    }

    @Specialization(replaces = {"intAssignment", "doubleAssignment", "boolAssignment"})
    protected Object objectAssignment(Frame currentOrParentFrame, Object value) {
        int frameSlot = this.getFrameSlot();
        currentOrParentFrame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Object);
        currentOrParentFrame.setObject(frameSlot, value);
        return value;
    }
}
