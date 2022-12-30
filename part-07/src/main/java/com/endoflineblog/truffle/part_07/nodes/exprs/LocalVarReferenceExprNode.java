package com.endoflineblog.truffle.part_07.nodes.exprs;

import com.endoflineblog.truffle.part_07.EasyScriptException;
import com.endoflineblog.truffle.part_07.nodes.stmts.LocalVarDeclStmtNode;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the reference to a variable local to a function.
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

    @Specialization(replaces = {"readInt", "readDouble"})
    protected Object readObject(VirtualFrame frame) {
        Object ret = frame.getObject(this.getFrameSlot());
        if (ret == LocalVarDeclStmtNode.DUMMY) {
            throw new EasyScriptException("Cannot access '" +
                    frame.getFrameDescriptor().getSlotName(this.getFrameSlot()) +
                    "' before initialization");
        }
        return ret;
    }
}
