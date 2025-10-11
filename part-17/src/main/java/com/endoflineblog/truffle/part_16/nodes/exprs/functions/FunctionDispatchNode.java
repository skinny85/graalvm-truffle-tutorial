package com.endoflineblog.truffle.part_16.nodes.exprs.functions;

import com.endoflineblog.truffle.part_16.common.ShapesAndPrototypes;
import com.endoflineblog.truffle.part_16.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_16.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_16.runtime.Environment;
import com.endoflineblog.truffle.part_16.runtime.FunctionObject;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A helper Node that contains specialization for functions calls.
 * Identical to the class with the same name from part 15.
 */
public abstract class FunctionDispatchNode extends EasyScriptNode {
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
            @Cached("create(function.callTarget)") DirectCallNode directCallNode,
            @Cached("currentLanguageContext().shapesAndPrototypes") ShapesAndPrototypes shapesAndPrototypes,
            @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary) {
        return directCallNode.call(closureArguments(arguments, receiver, function,
                shapesAndPrototypes, dynamicObjectLibrary));
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
            @Cached IndirectCallNode indirectCallNode,
            @Cached("currentLanguageContext().shapesAndPrototypes") ShapesAndPrototypes shapesAndPrototypes,
            @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary) {
        return indirectCallNode.call(function.callTarget, closureArguments(arguments, receiver, function,
                shapesAndPrototypes, dynamicObjectLibrary));
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

    private static Object[] closureArguments(
            Object[] arguments, Object receiver, FunctionObject function,
            ShapesAndPrototypes shapesAndPrototypes, DynamicObjectLibrary dynamicObjectLibrary) {
        Environment environment = new Environment(shapesAndPrototypes.rootShape);
        // move all arguments to the closure's environment
        for (int i = 0; i < function.argumentCount; i++) {
            // the argument read Nodes are offset by 1 because of 'this'
            // (that happens in the parser),
            // so we need to offset the arguments in the closure environment as well
            dynamicObjectLibrary.put(environment, i + 1,
                    i < arguments.length ? arguments[i] : Undefined.INSTANCE);
        }
        Object[] ret = new Object[function.parentEnvironment == null ? 2 : 3];
        ret[0] = receiver;
        int i = 1;
        if (function.parentEnvironment != null) {
            ret[i++] = function.parentEnvironment;
        }
        ret[i] = environment;
        return ret;
    }
}
