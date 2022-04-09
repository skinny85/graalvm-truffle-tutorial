package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

/**
 * The abstract common ancestor of all AST Nodes that represent statements in EasyScript,
 * like declaring a variable or constant.
 * Identical to the class with the same name from part 5.
 */
public abstract class EasyScriptStmtNode extends Node {
    /**
     * Evaluates this statement, and returns the result of executing it.
     */
    public abstract Object executeStatement(VirtualFrame frame);
}
