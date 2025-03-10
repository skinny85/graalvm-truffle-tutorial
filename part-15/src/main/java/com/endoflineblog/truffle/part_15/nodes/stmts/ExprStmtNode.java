package com.endoflineblog.truffle.part_15.nodes.stmts;

import com.endoflineblog.truffle.part_15.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_15.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node that represents an expression statement.
 * Almost identical to the class with the same name from part 14,
 * the only difference is that we override the {@link #getSourceSection()}
 * method from the {@link com.oracle.truffle.api.nodes.Node} class,
 * which is used for filling exception stack traces.
 */
public final class ExprStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode expr;
    private final SourceSection sourceSection;
    private final boolean discardExpressionValue;

    public ExprStmtNode(EasyScriptExprNode expr) {
        this(expr, null);
    }

    /**
     * Creates a new instance of the expression statement.
     *
     * @param expr the expression node
     */
    public ExprStmtNode(EasyScriptExprNode expr, SourceSection sourceSection) {
        this(expr, sourceSection, false);
    }

    /**
     * Creates a new instance of the expression statement.
     *
     * @param expr the expression node
     * @param discardExpressionValue whether evaluating this statement should discard
     *   the value of the expression it wraps,
     *   and always return {@code Undefined.INSTANCE}
     */
    public ExprStmtNode(EasyScriptExprNode expr, SourceSection sourceSection,
            boolean discardExpressionValue) {
        this.expr = expr;
        this.sourceSection = sourceSection;
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

    @Override
    public SourceSection getSourceSection() {
        return this.sourceSection;
    }
}
