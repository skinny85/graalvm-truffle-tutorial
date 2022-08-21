package com.endoflineblog.truffle.part_09.nodes.exprs.literals;

import com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_09.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * The AST node that represents the 'undefined' literal JavaScript expression.
 * Almost identical to the class with the same name from part 7,
 * the only difference is the additional {@link #executeBool}
 * method from {@link com.endoflineblog.truffle.part_09.nodes.exprs.EasyScriptExprNode}.
 *
 * @see #executeBool
 */
public final class UndefinedLiteralExprNode extends EasyScriptExprNode {
    @Override
    public boolean executeBool(VirtualFrame frame) {
        // undefined is falsy
        return false;
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(com.endoflineblog.truffle.part_09.runtime.Undefined.INSTANCE);
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        throw new UnexpectedResultException(com.endoflineblog.truffle.part_09.runtime.Undefined.INSTANCE);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return Undefined.INSTANCE;
    }
}
