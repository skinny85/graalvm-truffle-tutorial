package com.endoflineblog.truffle.part_16.nodes.stmts;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node that represents an expression statement.
 * Almost identical to the class with the same name from part 15,
 * the only difference is moving the {@link SourceSection}
 * from this class up to the parent class, {@link EasyScriptStmtNode}.
 */
public final class ExprStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode expr;

    /**
     * We now reference this field from {@link com.endoflineblog.truffle.part_16.nodes.stmts.blocks.LocalVarNodeVisitor},
     * so it needs to be {@code public}.
     */
    public final boolean discardExpressionValue;

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
        super(sourceSection);
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
