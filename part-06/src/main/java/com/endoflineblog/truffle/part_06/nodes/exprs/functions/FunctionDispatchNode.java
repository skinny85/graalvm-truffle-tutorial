package com.endoflineblog.truffle.part_06.nodes.exprs.functions;

import com.endoflineblog.truffle.part_06.EasyScriptException;
import com.endoflineblog.truffle.part_06.runtime.FunctionObject;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;

/**
 * A helper Node that contains specialization for functions calls.
 * Used by {@link FunctionCallExprNode},
 * and by {@link FunctionObject}.
 */
public abstract class FunctionDispatchNode extends Node {
    public abstract Object executeDispatch(Object function, Object[] arguments);

    /**
     * A specialization that calls the given target directly.
     * This is the fastest case,
     * used when the target of a call stays the same during the execution of the program,
     * like in {@code Math.abs(-3)}.
     */
    @Specialization(guards = "function.callTarget == directCallNode.getCallTarget()", limit = "2")
    protected static Object dispatchDirectly(
            @SuppressWarnings("unused") FunctionObject function,
            Object[] arguments,
            @Cached("create(function.callTarget)") DirectCallNode directCallNode) {
        return directCallNode.call(arguments);
    }

    /**
     * A specialization that calls the given target indirectly.
     * You might be surprised that we need this specialization at all -
     * won't the CallTarget of a given function never change?
     * But consider the following code: {@code (cond ? f1 : f2)(34)}.
     * Suddenly, based on the value of {@code cond},
     * the same expression can evaluate to different functions,
     * and that's why we need this specialization.
     */
    @Specialization(replaces = "dispatchDirectly")
    protected static Object dispatchIndirectly(
            FunctionObject function,
            Object[] arguments,
            @Cached IndirectCallNode indirectCallNode) {
        return indirectCallNode.call(function.callTarget, arguments);
    }

    /**
     * A fallback used in case the expression that's the target of the call
     * doesn't evaluate to a function.
     */
    @Fallback
    protected static Object targetIsNotAFunction(
            Object nonFunction,
            @SuppressWarnings("unused") Object[] arguments) {
        throw new EasyScriptException("'" + nonFunction + "' is not a function");
    }
}
