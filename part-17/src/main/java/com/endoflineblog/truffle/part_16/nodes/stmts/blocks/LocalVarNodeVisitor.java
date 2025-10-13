package com.endoflineblog.truffle.part_16.nodes.stmts.blocks;

import com.endoflineblog.truffle.part_16.nodes.exprs.variables.LocalVarAssignmentExprNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.ExprStmtNode;
import com.endoflineblog.truffle.part_16.runtime.debugger.LocalVarRefObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.nodes.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link NodeVisitor} that collects all local variable references in a given statement block.
 * Used in {@link BlockStmtNode} and {@link UserFuncBodyStmtNode}.
 */
public final class LocalVarNodeVisitor implements NodeVisitor {
    public final List<LocalVarRefObject> localVarRefs = new ArrayList<>(4);
    private boolean inDeclaration = false;

    @Override
    public boolean visit(Node visistedNode) {
        if (visistedNode instanceof ExprStmtNode) {
            var exprStmtNode = (ExprStmtNode) visistedNode;
            if (exprStmtNode.discardExpressionValue) {
                this.inDeclaration = true;
            }
            NodeUtil.forEachChild(visistedNode, this);
            this.inDeclaration = false;
            return true;
        }
        if (this.inDeclaration && visistedNode instanceof LocalVarAssignmentExprNode) {
            var lvaen = (LocalVarAssignmentExprNode) visistedNode;
            localVarRefs.add(new LocalVarRefObject(
                    lvaen.getSlotName(),
                    lvaen.getSourceSection(),
                    lvaen.getFrameSlot()));
            return true;
        }
        // Recur into any Node except a block of statements.
        if (!(visistedNode instanceof BlockStmtNode)) {
            NodeUtil.forEachChild(visistedNode, this);
        }
        return true;
    }
}
