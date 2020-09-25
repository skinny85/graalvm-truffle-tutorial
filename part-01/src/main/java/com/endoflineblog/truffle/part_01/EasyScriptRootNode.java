package com.endoflineblog.truffle.part_01;

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

    /**
     * The execute method for this AST.
     * Simply delegates to the {@link EasyScriptNode#executeInt}
     * method of the instance that was passed to it through the constructor.
     *
     * @param frame the reference to the activation record of the call stack,
     *   used for things like local variables
     * @return the value of executing this AST
     *   (in our case, it's going to always be an integer,
     *   as that's what {@link EasyScriptNode#executeInt} returns
     */
    @Override
    public Object execute(VirtualFrame frame) {
        return this.exprNode.executeInt(frame);
    }
}
