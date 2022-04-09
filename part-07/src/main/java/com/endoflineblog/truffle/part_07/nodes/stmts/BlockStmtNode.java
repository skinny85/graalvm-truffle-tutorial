package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.DeclarationKind;
import com.endoflineblog.truffle.part_07.nodes.exprs.GlobalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.UndefinedLiteralExprNode;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A Node that represents a block of statements.
 * Used for representing the contents of the entire program,
 * and also the body of a user-defined function.
 */
public final class BlockStmtNode extends EasyScriptStmtNode {
    @Children
    private final EasyScriptStmtNode[] stmts;

    public BlockStmtNode(List<EasyScriptStmtNode> stmts) {
        // implement hoisting of 'var' declarations,
        // see: https://developer.mozilla.org/en-US/docs/Glossary/Hoisting
        List<GlobalVarDeclStmtNode> varDeclarations = new LinkedList<>();
        List<EasyScriptStmtNode> remainingStmts = new LinkedList<>();
        for (EasyScriptStmtNode stmt : stmts) {
            if (stmt instanceof GlobalVarDeclStmtNode) {
                var varDeclaration = (GlobalVarDeclStmtNode) stmt;
                if (varDeclaration.getDeclarationKind() == DeclarationKind.VAR) {
                    // any 'var' declarations are replaced by two statements:
                    // the first is a declaration with the initializer as 'undefined',
                    // the second is an assignment expression for that variable,
                    // with the right-hand side of the assignment being the original initializer
                    varDeclarations.add(GlobalVarDeclStmtNodeGen.create(
                            new UndefinedLiteralExprNode(), varDeclaration.getName(), DeclarationKind.VAR));

                    remainingStmts.add(new ExprStmtNode(
                            GlobalVarAssignmentExprNodeGen.create(
                                    varDeclaration.getInitializerExpr(), varDeclaration.getName()),
                            // we pass 'true' here to make sure this expression statement returns 'undefined',
                            // instead of the right-hand expression value, when executed
                            true));

                    continue;
                }
            }
            remainingStmts.add(stmt);
        }

        this.stmts = Stream.concat(
                varDeclarations.stream(),
                remainingStmts.stream()
        ).toArray(EasyScriptStmtNode[]::new);
    }

    /**
     * Evaluating the block statement evaluates all statements inside it,
     * and returns the result of executing the last statement.
     */
    @Override
    @ExplodeLoop
    public Object executeStatement(VirtualFrame frame) {
        Object ret = Undefined.INSTANCE;
        for (EasyScriptStmtNode stmt : this.stmts) {
            ret = stmt.executeStatement(frame);
        }
        return ret;
    }
}
