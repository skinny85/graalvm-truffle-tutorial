package com.endoflineblog.truffle.part_06.nodes;

import com.endoflineblog.truffle.part_06.runtime.FunctionObject;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.Node;

public abstract class FunctionDispatchNode extends Node {
    public abstract Object executeDispatch(Object function, Object[] arguments);

    @Specialization
    protected static Object doIndirect(
            @SuppressWarnings("unused") FunctionObject function,
            Object[] arguments,
            @Cached("create(function.callTarget)") DirectCallNode callNode) {
        return callNode.call(arguments);
    }
}