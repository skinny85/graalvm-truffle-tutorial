package com.endoflineblog.truffle.part_09.exceptions;

import com.endoflineblog.truffle.part_09.nodes.stmts.controlflow.ReturnStmtNode;
import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code return} statement.
 *
 * @see ReturnStmtNode
 */
public final class ReturnException extends ControlFlowException {
    /** The value to return from the function. */
    public final Object returnValue;

    public ReturnException(Object returnValue) {
        this.returnValue = returnValue;
    }
}
