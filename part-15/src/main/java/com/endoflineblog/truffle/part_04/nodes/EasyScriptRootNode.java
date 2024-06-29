package com.endoflineblog.truffle.part_04.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * A Truffle AST must be anchored in a {@link RootNode}
 * to be executed.
 * Since {@link RootNode} is an abstract class,
 * you're expected to subclass it,
 * and override the abstract {@link #execute} method.
 *
 * @see #execute
 */
public final class EasyScriptRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptNode exprNode;

    public EasyScriptRootNode(EasyScriptNode exprNode) {
        super(null);

        this.exprNode = exprNode;
    }

    @Override
    public Integer execute(VirtualFrame frame) {
        return this.exprNode.executeInt(frame);
    }
}
