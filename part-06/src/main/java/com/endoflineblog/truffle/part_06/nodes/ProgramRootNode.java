package com.endoflineblog.truffle.part_06.nodes;

import com.endoflineblog.truffle.part_06.DeclarationKind;
import com.endoflineblog.truffle.part_06.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_06.nodes.exprs.GlobalVarAssignmentExprNodeGen;
import com.endoflineblog.truffle.part_06.nodes.exprs.UndefinedLiteralExprNode;
import com.endoflineblog.truffle.part_06.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_06.nodes.stmts.ExprStmtNode;
import com.endoflineblog.truffle.part_06.nodes.stmts.GlobalVarDeclStmtNode;
import com.endoflineblog.truffle.part_06.nodes.stmts.GlobalVarDeclStmtNodeGen;
import com.endoflineblog.truffle.part_06.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The {@link RootNode} used for the EasyScript program itself.
 * Identical to the EasyScriptRootNode from part 5.
 */
public final class ProgramRootNode extends RootNode {
    @Children
    private final EasyScriptStmtNode[] stmtNodes;

    public ProgramRootNode(EasyScriptTruffleLanguage truffleLanguage,
            List<EasyScriptStmtNode> stmtNodes) {
        super(truffleLanguage);

        List<GlobalVarDeclStmtNode> varDeclarations = new ArrayList<>();
        List<EasyScriptStmtNode> remainingStmts = new ArrayList<>();
        for (EasyScriptStmtNode stmtNode : stmtNodes) {
            if (stmtNode instanceof GlobalVarDeclStmtNode) {
                var varDeclaration = (GlobalVarDeclStmtNode) stmtNode;
                if (varDeclaration.getDeclarationKind() == DeclarationKind.VAR) {
                    varDeclarations.add(GlobalVarDeclStmtNodeGen.create(
                            new UndefinedLiteralExprNode(), varDeclaration.getName(), DeclarationKind.VAR));

                    remainingStmts.add(new ExprStmtNode(
                            GlobalVarAssignmentExprNodeGen.create(
                                    varDeclaration.getInitializerExpr(), varDeclaration.getName()),
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
