package com.endoflineblog.truffle.part_04.nodes;

import com.endoflineblog.truffle.part_04.EasyScriptTruffleLanguage;
import com.oracle.truffle.api.dsl.AOTSupport;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExecutionSignature;
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

    public EasyScriptRootNode(EasyScriptTruffleLanguage easyScriptTruffleLanguage,
            EasyScriptNode exprNode) {
        super(easyScriptTruffleLanguage);

        this.exprNode = exprNode;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.exprNode.executeGeneric(frame);
    }

    @Override
    protected ExecutionSignature prepareForAOT() {
        // prepare all child Nodes
        AOTSupport.prepareForAOT(this);

        return ExecutionSignature.create(Object.class, new Class[0]);
    }
}
