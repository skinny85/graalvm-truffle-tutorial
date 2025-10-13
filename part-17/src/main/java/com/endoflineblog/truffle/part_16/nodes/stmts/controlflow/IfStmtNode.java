package com.endoflineblog.truffle.part_16.nodes.stmts.controlflow;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A Node that represents an {@code if} statement.
 * Almost identical to the class with the same name from part 15,
 * the only difference is that we add a {@link SourceSection}
 * parameter to the constructor, which we pass to the constructor of the superclass,
 * {@link EasyScriptStmtNode}.
 */
public final class IfStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode conditionExpr;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode thenStmt;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptStmtNode elseStmt;

    private final ConditionProfile condition = ConditionProfile.createCountingProfile();

    public IfStmtNode(
            EasyScriptExprNode conditionExpr, EasyScriptStmtNode thenStmt,
            EasyScriptStmtNode elseStmt, SourceSection sourceSection) {
        super(sourceSection);
        this.conditionExpr = conditionExpr;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        if (this.condition.profile(this.conditionExpr.executeBool(frame))) {
            return this.thenStmt.executeStatement(frame);
        } else {
            return this.elseStmt == null
                    ? Undefined.INSTANCE
                    : this.elseStmt.executeStatement(frame);
        }
    }
}
