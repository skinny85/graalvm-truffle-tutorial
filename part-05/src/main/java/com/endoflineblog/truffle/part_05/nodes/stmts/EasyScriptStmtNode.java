package com.endoflineblog.truffle.part_05.nodes.stmts;

import com.endoflineblog.truffle.part_05.nodes.EasyScriptNode;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The abstract common ancestor of all AST Nodes that represent statements in EasyScript,
 * like declaring a variable or constant.
 */
public abstract class EasyScriptStmtNode extends EasyScriptNode {
    /**
     * Evaluates this statement, and returns the result of executing it.
     */
    public abstract Object executeStatement(VirtualFrame frame);
}
