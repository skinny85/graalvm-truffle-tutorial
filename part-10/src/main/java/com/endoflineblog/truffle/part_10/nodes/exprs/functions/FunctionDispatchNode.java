package com.endoflineblog.truffle.part_10.nodes.exprs.functions;

import com.endoflineblog.truffle.part_10.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_10.runtime.FunctionObject;
import com.endoflineblog.truffle.part_10.runtime.Undefined;
import com.oracle.truffle.api.Assumption;
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
 * Very similar to the class with the same name from part 8,
 * the only difference is we check the {@link Assumption}
 * returned from {@link FunctionObject#getFunctionWasNotRedefinedAssumption()}
 * method in the {@link #dispatchDirectly direct function dispatch code}.
 *
 * @see #dispatchDirectly
 */
public abstract class FunctionDispatchNode extends Node {
    public abstract Object executeDispatch(Object function, Object[] arguments);

    /**
     * A specialization that calls the given target directly.
     * This is the fastest case,
     * used when the target of a call stays the same during the execution of the program,
     * like in {@code Math.abs(-3)}.
     */
    @Specialization(
            guards = "function.getCallTarget() == directCallNode.getCallTarget()",
            limit = "2",
            assumptions = "functionWasNotRedefined"
    )
    protected static Object dispatchDirectly(
            FunctionObject function,
            Object[] arguments,
            @SuppressWarnings("unused") @Cached("function.getFunctionWasNotRedefinedAssumption()") Assumption functionWasNotRedefined,
            @Cached("create(function.getCallTarget())") DirectCallNode directCallNode) {
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
        return indirectCallNode.call(function.getCallTarget(), extendArguments(arguments, function));
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
        if (arguments.length >= function.getArgumentCount()) {
            return arguments;
        }
        Object[] ret = new Object[function.getArgumentCount()];
        for (int i = 0; i < function.getArgumentCount(); i++) {
            ret[i] = i < arguments.length
                    ? arguments[i]
                    : Undefined.INSTANCE;
        }
        return ret;
    }
}
