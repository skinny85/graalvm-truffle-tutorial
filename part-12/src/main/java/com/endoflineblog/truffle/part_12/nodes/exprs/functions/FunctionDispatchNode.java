package com.endoflineblog.truffle.part_12.nodes.exprs.functions;

import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.runtime.FunctionObject;
import com.endoflineblog.truffle.part_12.runtime.Undefined;
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
        // if the function object doesn't have a target
        // (meaning, it's a function, not method, call),
        // and we were called with at least as many arguments as the function takes,
        // there's nothing to do
        if (arguments.length >= function.argumentCount && function.methodTarget == null) {
            return arguments;
        }

        // otherwise, create a new arguments array,
        // saving the method target as the first element if it's non-null,
        // and filling out the remaining arguments with 'undefined's
        // if we got called with less of them than the function expects
        Object[] ret = new Object[function.argumentCount];
        for (int i = 0; i < function.argumentCount; i++) {
            int j;
            if (function.methodTarget == null) {
                j = i;
            } else {
                if (i == 0) {
                    ret[0] = function.methodTarget;
                    continue;
                } else {
                    j = i - 1;
                }
            }
            ret[i] = j < arguments.length ? arguments[j] : Undefined.INSTANCE;
        }
        return ret;
    }
}
