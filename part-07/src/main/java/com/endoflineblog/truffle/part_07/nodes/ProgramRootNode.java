package com.endoflineblog.truffle.part_07.nodes;

import com.endoflineblog.truffle.part_07.DeclarationKind;
import com.endoflineblog.truffle.part_07.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_07.nodes.exprs.GlobalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_07.nodes.exprs.UndefinedLiteralExprNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.ExprStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.GlobalVarDeclStmtNode;
import com.endoflineblog.truffle.part_07.nodes.stmts.GlobalVarDeclStmtNodeGen;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The {@link RootNode} used for the EasyScript program itself.
 * Identical to the EasyScriptRootNode class from part 5.
 */
public final class ProgramRootNode extends RootNode {
    @Children
    private final EasyScriptStmtNode[] stmtNodes;

    public ProgramRootNode(EasyScriptTruffleLanguage truffleLanguage,
            List<EasyScriptStmtNode> stmtNodes) {
        super(truffleLanguage);

        // implement hoisting of 'var' declarations,
        // see: https://developer.mozilla.org/en-US/docs/Glossary/Hoisting
        List<GlobalVarDeclStmtNode> varDeclarations = new ArrayList<>();
        List<EasyScriptStmtNode> remainingStmts = new ArrayList<>();
        for (EasyScriptStmtNode stmtNode : stmtNodes) {
            if (stmtNode instanceof GlobalVarDeclStmtNode) {
                var varDeclaration = (GlobalVarDeclStmtNode) stmtNode;
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
            remainingStmts.add(stmtNode);
        }

        this.stmtNodes = Stream.concat(
                varDeclarations.stream(),
                remainingStmts.stream()
        ).toArray(EasyScriptStmtNode[]::new);
    }

    /**
     * The result of executing an EasyScript program in this chapter of the tutorial
     * is simply the result of executing the last statement in the list.
     */
    @Override
    @ExplodeLoop
    public Object execute(VirtualFrame frame) {
        Object ret = Undefined.INSTANCE;
        for (EasyScriptStmtNode stmtNode : this.stmtNodes) {
            ret = stmtNode.executeStatement(frame);
        }
        return ret;
    }
}
