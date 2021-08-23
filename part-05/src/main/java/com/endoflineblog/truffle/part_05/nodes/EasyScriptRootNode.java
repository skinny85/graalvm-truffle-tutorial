package com.endoflineblog.truffle.part_05.nodes;

import com.endoflineblog.truffle.part_05.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_05.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import java.util.List;

/**
 * The {@link RootNode} for EasyScript code.
 * Contains a list of statements.
 */
public final class EasyScriptRootNode extends RootNode {
    /**
     * Fields annotated with {@code @Children}
     * need to have the array type.
     */
    @Children
    private final EasyScriptStmtNode[] stmtNodes;

    public EasyScriptRootNode(EasyScriptTruffleLanguage truffleLanguage,
            List<EasyScriptStmtNode> stmtNodes) {
        super(truffleLanguage);

        this.stmtNodes = stmtNodes.toArray(new EasyScriptStmtNode[]{});
    }

    /**
     * The result of executing an EasyScript program in this chapter of the tutorial
     * is simply the result of executing the last statement in the list.
     */
    @Override
    public Object execute(VirtualFrame frame) {
        Object ret = null;
        for (EasyScriptStmtNode stmtNode : this.stmtNodes) {
            ret = stmtNode.executeStatement(frame);
        }
        return ret;
    }
}
