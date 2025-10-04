package com.endoflineblog.truffle.part_16.nodes.exprs.functions;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.runtime.Environment;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

public abstract class WriteClosureArgExprNode extends EasyScriptExprNode {
    public final int index;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode initializerExpr;

    protected WriteClosureArgExprNode(EasyScriptExprNode initializerExpr, int index) {
        this.index = index;
        this.initializerExpr = initializerExpr;
    }

    @Specialization
    protected Object writeArgToClosureEnv(
            VirtualFrame frame,
            @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary) {
        Object value = this.initializerExpr.executeGeneric(frame);
        // because of the logic in FunctionDispatchNode,
        // we know the environment is always the second argument
        Environment environment = (Environment) frame.getArguments()[1];
        dynamicObjectLibrary.put(environment, this.index,  value);
        return value;
    }
}
