package com.endoflineblog.truffle.part_11.exceptions;

import com.endoflineblog.truffle.part_11.nodes.stmts.controlflow.BreakStmtNode;
import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code break} statement.
 * Identical to the class with the same name from part 9.
 *
 * @see BreakStmtNode
 */
public final class BreakException extends ControlFlowException {
}
