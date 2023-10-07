package com.endoflineblog.truffle.part_13.nodes.exprs.functions;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_13.runtime.FunctionObject;
import com.endoflineblog.truffle.part_13.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;

/**
 * A helper Node that contains specialization for functions calls.
 * Identical to the class with the same name from part 11.
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
            FunctionObject function,
            Object[] arguments,
            @Cached("create(function.callTarget)") DirectCallNode directCallNode) {
        return directCallNode.call(extendArguments(arguments, function));
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
        return indirectCallNode.call(function.callTarget, extendArguments(arguments, function));
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

    private static Object[] extendArguments(Object[] arguments, FunctionObject function) {
        // create a new array of arguments, reserving the first one for 'this',
        // which means we need to offset the remaining arguments by one
        int extendedArgumentsLength = function.argumentCount +
                (function.methodTarget == null ? 1 : 0);
        Object[] ret = new Object[extendedArgumentsLength];
        for (int i = 0; i < extendedArgumentsLength; i++) {
            if (i == 0) {
                // for 'this', if we don't have a method target, we need to use 'undefined'
                ret[i] = function.methodTarget == null ? Undefined.INSTANCE : function.methodTarget;
            } else {
                // we need to offset the arguments by one
                int j = i - 1;
                ret[i] = j < arguments.length
                        ? arguments[j]
                        // if a function was called with fewer arguments than it declares,
                        // we fill them with `undefined`
                        : Undefined.INSTANCE;
            }
        }
        return ret;
    }
}
