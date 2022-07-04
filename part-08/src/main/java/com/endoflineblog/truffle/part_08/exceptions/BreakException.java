package com.endoflineblog.truffle.part_08.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code break} statement.
 *
 * @see com.endoflineblog.truffle.part_08.nodes.stmts.controlflow.BreakStmtNode
 */
public final class BreakException extends ControlFlowException {
}
