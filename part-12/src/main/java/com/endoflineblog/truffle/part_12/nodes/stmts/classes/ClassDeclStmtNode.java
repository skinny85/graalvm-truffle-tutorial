package com.endoflineblog.truffle.part_12.nodes.stmts.classes;

import com.endoflineblog.truffle.part_12.nodes.exprs.GlobalScopeObjectExprNode;
import com.endoflineblog.truffle.part_12.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_12.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeField(name = "classPrototypeObject", type = ClassPrototypeObject.class)
@NodeField(name = "classMethodDecls", type = EasyScriptStmtNode[].class)
public abstract class ClassDeclStmtNode extends EasyScriptStmtNode {
    @CompilationFinal
    private boolean cached;

    protected abstract ClassPrototypeObject getClassPrototypeObject();
    protected abstract EasyScriptStmtNode[] getClassMethodDecls();

    @ExplodeLoop
    @Specialization(limit = "1")
    protected Object declareClass(VirtualFrame frame, DynamicObject globalScopeObject,
                @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        if (!this.cached) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            for (var classMethodDecl : this.getClassMethodDecls()) {
                classMethodDecl.executeStatement(frame);
            }
            this.cached = true;
        }

        // classes can be reassigned as regular values
        ClassPrototypeObject classPrototypeObject = this.getClassPrototypeObject();
        objectLibrary.putConstant(globalScopeObject, classPrototypeObject.className, classPrototypeObject, 0);

        // we return 'undefined' for statements that declare classes
        return Undefined.INSTANCE;
    }
}
