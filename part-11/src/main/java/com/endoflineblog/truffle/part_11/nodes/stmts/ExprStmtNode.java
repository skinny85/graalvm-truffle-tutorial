package com.endoflineblog.truffle.part_11.nodes.stmts;

import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

/**
 * A Node that represents an expression statement.
 * Identical to the class with the same name from part 10.
 */
public final class ExprStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Node.Child
    private EasyScriptExprNode expr;
    private final boolean discardExpressionValue;

    /**
     * Creates a new instance of the expression statement.
     *
     * @param expr the expression node
     */
    public ExprStmtNode(EasyScriptExprNode expr) {
        this(expr, false);
    }

    /**
     * Creates a new instance of the expression statement.
     *
     * @param expr the expression node
     * @param discardExpressionValue whether evaluating this statement should discard
     *   the value of the expression it wraps,
     *   and always return {@code Undefined.INSTANCE}
     */
    public ExprStmtNode(EasyScriptExprNode expr, boolean discardExpressionValue) {
        this.expr = expr;
        this.discardExpressionValue = discardExpressionValue;
    }

    /** Evaluating the statement returns the result of executing its expression. */
    @Override
    public Object executeStatement(VirtualFrame frame) {
        Object exprResult = this.expr.executeGeneric(frame);
        // if this statement was created because of hoisting a variable declaration,
        // return 'undefined', to be consistent with how other declarations work
        return this.discardExpressionValue ? Undefined.INSTANCE : exprResult;
    }
}
