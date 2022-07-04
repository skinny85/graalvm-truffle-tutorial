package com.endoflineblog.truffle.part_08.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code continue} statement.
 *
 * @see com.endoflineblog.truffle.part_08.nodes.stmts.controlflow.ContinueStmtNode
 */
public final class ContinueException extends ControlFlowException {
}
