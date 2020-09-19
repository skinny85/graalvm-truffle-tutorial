package com.endoflineblog.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;

public class AdditionNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode leftNode, rightNode;

    public AdditionNode(EasyScriptExprNode leftNode, EasyScriptExprNode rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    @Override
    public int executeInt(VirtualFrame frame) {
        int leftValue = this.leftNode.executeInt(frame);
        int rightValue = this.rightNode.executeInt(frame);
        return leftValue + rightValue;
    }
}
