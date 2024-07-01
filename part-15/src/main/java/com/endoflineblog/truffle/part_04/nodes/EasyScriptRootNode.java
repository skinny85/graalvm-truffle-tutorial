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
    public Integer execute(VirtualFrame frame) {
        return this.exprNode.executeInt(frame);
    }

    @Override
    protected ExecutionSignature prepareForAOT() {
        // prepare all child Nodes
        AOTSupport.prepareForAOT(this);

        // this must be Integer.class -
        // int.class would cause the compiled code to invalidate
        return ExecutionSignature.create(Integer.class, new Class[0]);
    }
}
