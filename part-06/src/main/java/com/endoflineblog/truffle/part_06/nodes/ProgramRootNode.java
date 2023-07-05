package com.endoflineblog.truffle.part_06.nodes;

import com.endoflineblog.truffle.part_06.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_06.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_06.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;

import java.util.List;

/**
 * The {@link RootNode} used for the EasyScript program itself.
 * Identical to the EasyScriptRootNode class from part 5.
 */
public final class ProgramRootNode extends RootNode {
    @Children
    private final EasyScriptStmtNode[] stmtNodes;

    public ProgramRootNode(EasyScriptTruffleLanguage truffleLanguage,
            List<EasyScriptStmtNode> stmtNodes) {
        super(truffleLanguage);

        this.stmtNodes = stmtNodes.toArray(new EasyScriptStmtNode[]{});
    }

    /**
     * The result of executing an EasyScript program in this chapter of the tutorial
     * is simply the result of executing the last statement in the list.
     */
    @Override
    @ExplodeLoop
    public Object execute(VirtualFrame frame) {
        Object ret = Undefined.INSTANCE;
        for (EasyScriptStmtNode stmtNode : this.stmtNodes) {
            ret = stmtNode.executeStatement(frame);
        }
        return ret;
    }
}
