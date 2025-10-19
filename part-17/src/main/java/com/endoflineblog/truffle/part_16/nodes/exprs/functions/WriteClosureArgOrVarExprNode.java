package com.endoflineblog.truffle.part_16.nodes.exprs.functions;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.runtime.Environment;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

public abstract class WriteClosureArgOrVarExprNode extends EasyScriptExprNode {
    private final int funcNestingLevel;
    public final String varName;
    public final int argIndex;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode initializerExpr;

    protected WriteClosureArgOrVarExprNode(EasyScriptExprNode initializerExpr, int funcNestingLevel,
            String varName, int argIndex) {
        this.funcNestingLevel = funcNestingLevel;
        this.varName = varName;
        this.argIndex = argIndex;
        this.initializerExpr = initializerExpr;
    }

    @Specialization
    protected Object writeArgToClosureEnv(
            VirtualFrame frame,
            @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary) {
        Object value = this.initializerExpr.executeGeneric(frame);
        // because of the logic in FunctionDispatchNode,
        // we know the environment is always the second argument
        var environment = (Environment) frame.getArguments()[this.funcNestingLevel];
        dynamicObjectLibrary.put(environment, this.argIndex,  value);
        return value;
    }
}
