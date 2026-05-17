package com.endoflineblog.truffle.part_16.nodes.stmts.variables;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.nodes.exprs.functions.FunctionDefinitionExprNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A Node that represents the declaration of a function in EasyScript.
 * Almost identical to the class with the same name from part 15,
 * the only difference is that we override the {@link #hasTag}
 * method to return {@code false} for all possible tags,
 * so that this statement is ignored by the debugger.
 */
@NodeChild(value = "containerObjectExpr", type = EasyScriptExprNode.class)
@NodeChild(value = "functionDefinitionExpr", type = FunctionDefinitionExprNode.class)
@NodeField(name = "funcName", type = String.class)
public abstract class FuncDeclStmtNode extends EasyScriptStmtNode {
    protected abstract String getFuncName();

    protected FuncDeclStmtNode() {
        // deliberately pass 'null' here,
        // as we don't want the debugger to stop on function declarations
        super(null);
    }

    @Specialization(limit = "2")
    protected Object declareFunction(DynamicObject containerObject, Object function,
            @CachedLibrary("containerObject") DynamicObjectLibrary objectLibrary) {
        // we allow functions to be redefined, to comply with JavaScript semantics
        objectLibrary.putConstant(containerObject, this.getFuncName(), function, 0);

        // we return 'undefined' for statements that declare functions
        return Undefined.INSTANCE;
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        // since we don't provide a SourceSection for function declarations,
        // we need to stop providing a 'StatementTag' for them
        return false;
    }
}
