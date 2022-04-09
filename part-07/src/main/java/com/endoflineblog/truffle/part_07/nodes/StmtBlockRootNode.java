package com.endoflineblog.truffle.part_07.nodes;

import com.endoflineblog.truffle.part_07.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_07.nodes.stmts.BlockStmtNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * dsads
 */
public final class StmtBlockRootNode extends RootNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockStmtNode blockStmt;

    public StmtBlockRootNode(EasyScriptTruffleLanguage truffleLanguage,
            BlockStmtNode blockStmt) {
        super(truffleLanguage);

        this.blockStmt = blockStmt;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.blockStmt.executeStatement(frame);
    }
}
