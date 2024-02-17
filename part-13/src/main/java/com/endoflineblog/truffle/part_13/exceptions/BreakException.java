package com.endoflineblog.truffle.part_13.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code break} statement.
 * Identical to the class with the same name from part 11.
 *
 * @see com.endoflineblog.truffle.part_13.nodes.stmts.controlflow.BreakStmtNode
 */
public final class BreakException extends ControlFlowException {
}
