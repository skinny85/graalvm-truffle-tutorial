package com.endoflineblog.truffle.part_06.nodes.exprs.functions;

import com.endoflineblog.truffle.part_06.EasyScriptException;
import com.endoflineblog.truffle.part_06.runtime.FunctionObject;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.Node;

public abstract class FunctionDispatchNode extends Node {
    public abstract Object executeDispatch(Object function, Object[] arguments);

    @Specialization
    protected static Object dispatchToFunctionObject(
            @SuppressWarnings("unused") FunctionObject function,
            Object[] arguments,
            @Cached("create(function.callTarget)") DirectCallNode callNode) {
        return callNode.call(arguments);
    }

    @Fallback
    protected static Object targetIsNotAFunction(
            Object nonFunction,
            @SuppressWarnings("unused") Object[] arguments) {
        throw new EasyScriptException("'" + nonFunction + "' is not a function");
    }
}
