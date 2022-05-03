package com.endoflineblog.truffle.part_07.nodes.exprs;

import com.endoflineblog.truffle.part_07.EasyScriptException;
import com.endoflineblog.truffle.part_07.nodes.stmts.LocalVarDeclStmtNode;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeField(name = "frameSlot", type = FrameSlot.class)
public abstract class LocalVarReferenceExprNode extends EasyScriptExprNode {
    protected abstract FrameSlot getFrameSlot();

    @Specialization(guards = "frame.isInt(getFrameSlot())")
    protected int readInt(VirtualFrame frame) {
        return FrameUtil.getIntSafe(frame, this.getFrameSlot());
    }

    @Specialization(guards = "frame.isDouble(getFrameSlot())", replaces = "readInt")
    protected double readDouble(VirtualFrame frame) {
        return FrameUtil.getDoubleSafe(frame, this.getFrameSlot());
    }

    @Fallback
    protected Object readObject(VirtualFrame frame) {
        Object ret = FrameUtil.getObjectSafe(frame, this.getFrameSlot());
        if (ret == LocalVarDeclStmtNode.DUMMY) {
            throw new EasyScriptException("Cannot access '" + this.getFrameSlot().getIdentifier() + "' before initialization");
        }
        return ret;
    }
}
