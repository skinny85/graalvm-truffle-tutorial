package com.endoflineblog.truffle.part_15.nodes.exprs.functions;

import com.endoflineblog.truffle.part_15.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_15.runtime.FunctionObject;
import com.endoflineblog.truffle.part_15.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;

/**
 * A helper Node that contains specialization for functions calls.
 * Identical to the class with the same name from part 14.
 */
public abstract class FunctionDispatchNode extends Node {
    /**
     * The execution method for this Node.
     * The {@code receiver} parameter will be passed as {@code this}
     * to the underlying function.
     */
    public abstract Object executeDispatch(Object function, Object[] arguments, Object receiver);

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
            Object receiver,
            @Cached("create(function.callTarget)") DirectCallNode directCallNode) {
        return directCallNode.call(extendArguments(arguments, receiver, function));
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
            Object receiver,
            @Cached IndirectCallNode indirectCallNode) {
        return indirectCallNode.call(function.callTarget, extendArguments(arguments, receiver, function));
    }

    /**
     * A fallback used in case the expression that's the target of the call
     * doesn't evaluate to a function.
     */
    @Fallback
    protected static Object targetIsNotAFunction(
            Object nonFunction,
            @SuppressWarnings("unused") Object[] arguments,
            @SuppressWarnings("unused") Object receiver) {
        throw new EasyScriptException("'" + nonFunction + "' is not a function");
    }

    private static Object[] extendArguments(Object[] arguments, Object receiver, FunctionObject function) {
        // create a new array of arguments, reserving the first one for 'this',
        // which means we need to offset the remaining arguments by one
        int extendedArgumentsLength = function.argumentCount + 1;
        Object[] ret = new Object[extendedArgumentsLength];
        // the first argument to a subroutine call is always 'this',
        // which is 'undefined' for global functions
        ret[0] = receiver;
        for (int i = 1; i < extendedArgumentsLength; i++) {
            // we need to offset the provided arguments by one, because of 'this'
            int j = i - 1;
            // if a function was called with fewer arguments than it declares,
            // we fill the remaining ones with `undefined`
            ret[i] = j < arguments.length ? arguments[j] : Undefined.INSTANCE;
        }
        return ret;
    }
}
