package com.endoflineblog.truffle.part_08.nodes.stmts;

import com.endoflineblog.truffle.part_08.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_08.runtime.Undefined;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;

public final class WhileStmtNode extends EasyScriptStmtNode {
    @Child
    private LoopNode loopNode;

    public WhileStmtNode(EasyScriptExprNode conditionExpr, EasyScriptStmtNode bodyStmt) {
        this.loopNode = Truffle.getRuntime().createLoopNode(new WhileRepeatingNode(conditionExpr, bodyStmt));
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        this.loopNode.execute(frame);
        return Undefined.INSTANCE;
    }
}
