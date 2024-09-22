package com.endoflineblog.truffle.part_15.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code continue} statement.
 * Identical to the class with the same name from part 14.
 *
 * @see com.endoflineblog.truffle.part_15.nodes.stmts.controlflow.ContinueStmtNode
 */
public final class ContinueException extends ControlFlowException {
}
