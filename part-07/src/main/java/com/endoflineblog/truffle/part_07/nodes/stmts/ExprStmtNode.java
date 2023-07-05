package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents an expression statement.
 * Very similar to the class with the same name from part 6,
 * the only difference is the {@link #ExprStmtNode(EasyScriptExprNode, boolean)}
 * constructor that allows discarding the value of the child expression when executing this statement,
 * and returning {@link Undefined}.
 * This is needed for local variable declaration statements,
 * which are turned to assignments during parsing,
 * but which should still return {@link Undefined}
 * when evaluated (unlike "regular" assignment expressions).
 */
public final class ExprStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
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
        // if this statement was created because of transforming a local variable declaration into an assignment,
        // return 'undefined', to be consistent with how other declarations work
        return this.discardExpressionValue ? Undefined.INSTANCE : exprResult;
    }
}
