package com.endoflineblog.truffle.part_17.nodes.exprs.variables;

import com.endoflineblog.truffle.part_17.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_17.nodes.exprs.frame.AbstractFrameGetNode;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlotKind;

/**
 * A Node that represents the assignment to a variable local to a function.
 * The typed specializations are split into two each: a "hot path" that fires
 * once the slot's kind has stabilized (and only calls the typed setter -
 * crucially, NOT {@code setSlotKind}), and a "first write" path that
 * transitions the slot's kind from {@code Illegal} to the typed kind.
 * Calling {@code setSlotKind} on every write would defeat partial-evaluation
 * constant-folding of {@code getSlotKind} reads in the guards
 * (because Graal would see the {@link com.oracle.truffle.api.frame.FrameDescriptor}
 * metadata as recently written), and would also cost a real memory write
 * to shared {@link com.oracle.truffle.api.frame.FrameDescriptor} state on
 * every loop iteration. This matches the structure used by GraalJS in
 * {@code JSWriteFrameSlotNode.doInteger} and friends.
 */
@NodeChild(value = "currentOrParentFrameGetNode", type = AbstractFrameGetNode.class)
@NodeChild("initializerExpr")
@NodeField(name = "slotName", type = String.class)
@NodeField(name = "frameSlot", type = int.class)
@ImportStatic(FrameSlotKind.class)
public abstract class LocalVarAssignmentExprNode extends EasyScriptExprNode {
    /**
     * We reference this field in {@link com.endoflineblog.truffle.part_17.nodes.stmts.blocks.LocalVarNodeVisitor},
     * so we need to make it {@code public}.
     */
    public abstract String getSlotName();

    /**
     * This method is now used in {@link com.endoflineblog.truffle.part_17.nodes.stmts.blocks.LocalVarNodeVisitor},
     * so we change it to be {@code public}.
     */
    public abstract int getFrameSlot();

    @Specialization(guards = "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Int")
    protected int intAssignment(Frame currentOrParentFrame, int value) {
        currentOrParentFrame.setInt(this.getFrameSlot(), value);
        return value;
    }

    @Specialization(guards = "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal")
    protected int intAssignmentFirstWrite(Frame currentOrParentFrame, int value) {
        int frameSlot = this.getFrameSlot();
        currentOrParentFrame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Int);
        currentOrParentFrame.setInt(frameSlot, value);
        return value;
    }

    @Specialization(guards = "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Double",
            replaces = "intAssignment")
    protected double doubleAssignment(Frame currentOrParentFrame, double value) {
        currentOrParentFrame.setDouble(this.getFrameSlot(), value);
        return value;
    }

    @Specialization(guards = "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal",
            replaces = "intAssignment")
    protected double doubleAssignmentFirstWrite(Frame currentOrParentFrame, double value) {
        int frameSlot = this.getFrameSlot();
        currentOrParentFrame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Double);
        currentOrParentFrame.setDouble(frameSlot, value);
        return value;
    }

    @Specialization(guards = "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Boolean")
    protected boolean boolAssignment(Frame currentOrParentFrame, boolean value) {
        currentOrParentFrame.setBoolean(this.getFrameSlot(), value);
        return value;
    }

    @Specialization(guards = "currentOrParentFrame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal")
    protected boolean boolAssignmentFirstWrite(Frame currentOrParentFrame, boolean value) {
        int frameSlot = this.getFrameSlot();
        currentOrParentFrame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Boolean);
        currentOrParentFrame.setBoolean(frameSlot, value);
        return value;
    }

    @Specialization(replaces = {
            "intAssignment", "intAssignmentFirstWrite",
            "doubleAssignment", "doubleAssignmentFirstWrite",
            "boolAssignment", "boolAssignmentFirstWrite"})
    protected Object objectAssignment(Frame currentOrParentFrame, Object value) {
        int frameSlot = this.getFrameSlot();
        currentOrParentFrame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Object);
        currentOrParentFrame.setObject(frameSlot, value);
        return value;
    }
}
