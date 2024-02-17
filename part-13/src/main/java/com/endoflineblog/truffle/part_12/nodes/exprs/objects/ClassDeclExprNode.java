package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.variables.FuncDeclStmtNode;
import com.endoflineblog.truffle.part_12.runtime.ClassPrototypeObject;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * The Node for handling a class declaration.
 * It simply handles methods inside the class by delegating to
 * {@link FuncDeclStmtNode}.
 */
public final class ClassDeclExprNode extends EasyScriptExprNode {
    @Children
    private final FuncDeclStmtNode[] classMethodDecls;

    private final ClassPrototypeObject classPrototypeObject;

    public ClassDeclExprNode(List<FuncDeclStmtNode> classMethodDecls,
            ClassPrototypeObject classPrototypeObject) {
        this.classMethodDecls = classMethodDecls.toArray(FuncDeclStmtNode[]::new);
        this.classPrototypeObject = classPrototypeObject;
    }

    @Override
    @ExplodeLoop
    public ClassPrototypeObject executeGeneric(VirtualFrame frame) {
        for (var classMethodDecl : this.classMethodDecls) {
            classMethodDecl.executeStatement(frame);
        }

        return this.classPrototypeObject;
    }
}
