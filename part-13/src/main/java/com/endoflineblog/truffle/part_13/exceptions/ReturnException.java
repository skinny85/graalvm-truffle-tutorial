package com.endoflineblog.truffle.part_13.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * The exception used to implement the {@code return} statement.
 * Identical to the class with the same name from part 12.
 *
 * @see com.endoflineblog.truffle.part_13.nodes.stmts.controlflow.ReturnStmtNode
 */
public final class ReturnException extends ControlFlowException {
    /** The value to return from the function. */
    public final Object returnValue;

    public ReturnException(Object returnValue) {
        this.returnValue = returnValue;
    }
}
