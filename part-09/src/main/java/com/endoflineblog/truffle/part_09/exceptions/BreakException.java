package com.endoflineblog.truffle.part_09.exceptions;

import com.endoflineblog.truffle.part_09.nodes.stmts.controlflow.BreakStmtNode;
import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code break} statement.
 *
 * @see BreakStmtNode
 */
public final class BreakException extends ControlFlowException {
}
