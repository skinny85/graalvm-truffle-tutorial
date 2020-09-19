package com.endoflineblog.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class EasyScriptRootNode extends RootNode {
    private final EasyScriptExprNode exprNode;

    public EasyScriptRootNode(EasyScriptExprNode exprNode) {
        super(null);

        this.exprNode = exprNode;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.exprNode.executeInt(frame);
    }
}
