package com.endoflineblog.truffle.part_12.exceptions;

import com.endoflineblog.truffle.part_12.nodes.stmts.controlflow.ContinueStmtNode;
import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code continue} statement.
 * Identical to the class with the same name from part 10.
 *
 * @see ContinueStmtNode
 */
public final class ContinueException extends ControlFlowException {
}
