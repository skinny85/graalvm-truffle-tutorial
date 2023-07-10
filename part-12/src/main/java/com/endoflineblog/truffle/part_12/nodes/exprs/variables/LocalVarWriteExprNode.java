package com.endoflineblog.truffle.part_12.nodes.exprs.variables;

import com.endoflineblog.truffle.part_12.common.Affix;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.ops.EasyScriptBinaryNumberOperationNode;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeChild("rvalueExpr")
@NodeField(name = "frameSlot", type = int.class)
@NodeField(name = "affix", type = Affix.class)
@ImportStatic(FrameSlotKind.class)
public abstract class LocalVarWriteExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    protected EasyScriptBinaryNumberOperationNode operation;

    protected LocalVarWriteExprNode(EasyScriptBinaryNumberOperationNode operation) {
        this.operation = operation;
    }

    protected abstract int getFrameSlot();

    protected abstract Affix getAffix();

    @Specialization(rewriteOn = ArithmeticException.class,
            guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
                    "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Int")
    protected int writeInt(VirtualFrame frame, int rvalue) {
        int frameSlot = this.getFrameSlot();
        if (this.operation == null) {
            // this can be the initial write to this variable
            frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Int);
            frame.setInt(frameSlot, rvalue);
            return rvalue;
        } else {
            // we know that local variables are always initialized,
            // so the frame slot kind is for sure Int here
            int prevValue = frame.getInt(frameSlot);
            int newValue = this.operation.executeOperationInt(frame, prevValue, rvalue);
            frame.setInt(frameSlot, newValue);
            return this.getAffix() == Affix.PREFIX ? newValue : prevValue;
        }
    }

    @Specialization(replaces = "writeInt",
            guards = "(frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
                    "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Int) || " +
                    "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Double")
    protected double writeDouble(VirtualFrame frame, double rvalue) {
        int frameSlot = this.getFrameSlot();
        if (this.operation == null) {
            // this can be the initial write to this variable
            frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Double);
            frame.setDouble(frameSlot, rvalue);
            return rvalue;
        } else {
            // in theory, this specialization can be activated if the operation on its overflows;
            // for that reason, we can't assume the frame slot kind is always Double here
            double prevValue = frame.getFrameDescriptor().getSlotKind(frameSlot) == FrameSlotKind.Int
                    ? (double) frame.getInt(frameSlot)
                    : frame.getDouble(frameSlot);
            double newValue = this.operation.executeOperationDouble(frame, prevValue, rvalue);
            frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Double);
            frame.setDouble(frameSlot, newValue);
            return this.getAffix() == Affix.PREFIX ? newValue : prevValue;
        }
    }

    /**
     * The boolean specializations can never be activated for in/decrement
     * (so when {@code this.operation != null}),
     * because the {@code rvalue} will be a 1 in this case, not a boolean.
     */
    @Specialization(guards = {
            "operation == null",
            "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Illegal || " +
                    "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Boolean"
    })
    protected Object writeBool(VirtualFrame frame, boolean rvalue) {
        int frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Boolean);
        frame.setBoolean(frameSlot, rvalue);
        return rvalue;
    }

    @Specialization(replaces = {"writeInt", "writeDouble", "writeBool"})
    protected Object writeObject(VirtualFrame frame, Object rvalue) {
        int frameSlot = this.getFrameSlot();
        if (this.operation == null) {
            frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Object);
            frame.setObject(frameSlot, rvalue);
            return rvalue;
        } else {
            // it could happen that the frame slot kind is Bool here,
            // if the program is in/decrementing a variable containing a boolean value
            Object prevValue = frame.getFrameDescriptor().getSlotKind(frameSlot) == FrameSlotKind.Boolean
                    ? frame.getBoolean(frameSlot)
                    : frame.getObject(frameSlot);
            Object newValue = this.operation.executeOperation(frame, prevValue, rvalue);
            frame.getFrameDescriptor().setSlotKind(frameSlot, FrameSlotKind.Object);
            frame.setObject(frameSlot, newValue);
            return this.getAffix() == Affix.PREFIX ? newValue : prevValue;
        }
    }
}
