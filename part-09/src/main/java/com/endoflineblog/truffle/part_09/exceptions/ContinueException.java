package com.endoflineblog.truffle.part_09.exceptions;

import com.endoflineblog.truffle.part_09.nodes.stmts.controlflow.ContinueStmtNode;
import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code continue} statement.
 *
 * @see ContinueStmtNode
 */
public final class ContinueException extends ControlFlowException {
}
