package com.endoflineblog.truffle.part_05.nodes.stmts;

import com.endoflineblog.truffle.part_05.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_05.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

/** A Node that represents an expression statement. */
public final class ExprStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode expr;
    private final boolean isHoistedDeclExpression;

    /**
     * Creates a new instance of the expression statement.
     *
     * @param expr the expression node
     * @param isHoistedDeclExpression whether this statement was created because of splitting
     *   a 'var' declaration into variable creation and assignment
     */
    public ExprStmtNode(EasyScriptExprNode expr, boolean isHoistedDeclExpression) {
        this.expr = expr;
        this.isHoistedDeclExpression = isHoistedDeclExpression;
    }

    /** Evaluating the statement returns the result of executing its expression. */
    @Override
    public Object executeStatement(VirtualFrame frame) {
        Object exprResult = this.expr.executeGeneric(frame);
        // if this statement was created because of hoisting a 'var' declaration to the top,
        // return 'undefined', to be consistent with how 'let' and 'const' declarations work
        return this.isHoistedDeclExpression ? Undefined.INSTANCE : exprResult;
    }
}
