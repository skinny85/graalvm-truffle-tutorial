package com.endoflineblog.truffle.part_12.nodes.exprs.variables;

import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.ops.EasyScriptOperationNode;
import com.endoflineblog.truffle.part_12.nodes.ops.IncrementOperationNodeGen;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "frameSlot", type = int.class)
@ImportStatic(FrameSlotKind.class)
public abstract class PostIncLocalVarExprNode extends EasyScriptExprNode {
    private final EasyScriptOperationNode operation;

    protected PostIncLocalVarExprNode() {
        this.operation = IncrementOperationNodeGen.create();
    }

    protected abstract int getFrameSlot();

    @Specialization(guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Int",
            rewriteOn = ArithmeticException.class)
    protected int intIncrement(VirtualFrame frame) {
        int frameSlot = this.getFrameSlot();
        int prevValue = frame.getInt(frameSlot);
        int newValue = this.operation.executeOperationInt(frame, prevValue);
        frame.setInt(frameSlot, newValue); // we know the kind of the slot is Int
        return prevValue;
    }

    @Specialization(replaces = "intIncrement",
            guards = "frame.getFrameDescriptor().getSlotKind(getFrameSlot()) == Double")
    protected double doubleIncrement(VirtualFrame frame) {
        int frameSlot = this.getFrameSlot();
        double prevValue = frame.getDouble(frameSlot);
        double newValue = this.operation.executeOperationDouble(frame, prevValue);
        frame.setDouble(frameSlot, newValue); // we know the kind of the slot is Double
        return prevValue;
    }

    @Specialization(replaces = {"intIncrement", "doubleIncrement"})
    protected Object genericIncrement(VirtualFrame frame) {
        int frameSlot = this.getFrameSlot();
        Object prevValue = frame.getObject(frameSlot);
        Object newValue = this.operation.executeOperation(frame, prevValue);
        frame.setObject(frameSlot, newValue);
        return prevValue;
    }
}
