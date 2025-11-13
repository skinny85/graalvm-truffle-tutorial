package com.endoflineblog.truffle.part_16.nodes.exprs.variables;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class ClosureLocalVarReferenceExprNode extends EasyScriptExprNode {
    private final int frameSlot;

    public ClosureLocalVarReferenceExprNode(int frameSlot) {
        this.frameSlot = frameSlot;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object frameArgument = frame.getArguments()[1];
        MaterializedFrame materializedFrame = (MaterializedFrame) frameArgument;
        return materializedFrame.getObject(this.frameSlot);
    }
}
