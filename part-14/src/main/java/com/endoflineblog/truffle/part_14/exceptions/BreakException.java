package com.endoflineblog.truffle.part_14.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code break} statement.
 * Identical to the class with the same name from part 13.
 *
 * @see com.endoflineblog.truffle.part_14.nodes.stmts.controlflow.BreakStmtNode
 */
public final class BreakException extends ControlFlowException {
}
