package com.endoflineblog.truffle.part_07.nodes.exprs;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class LocalVarReferenceExprNode extends EasyScriptExprNode {
    private final FrameSlot frameSlot;

    public LocalVarReferenceExprNode(FrameSlot frameSlot) {
        this.frameSlot = frameSlot;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return frame.getValue(this.frameSlot); // ToDo add specializations handling
    }
}
