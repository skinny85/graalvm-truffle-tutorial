package com.endoflineblog.truffle.part_11.runtime;

import com.endoflineblog.truffle.part_11.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_11.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The object that represents a function in EasyScript.
 * Almost identical to the class with the same name from part 8
 * (but different from the class with the same name from part 9,
 * as it's no longer mutable),
 * the only difference is adding {@link ArrayObject}
 * to the list of allowed EasyScript values when calling this function through the GraalVM polyglot API.
 */
@ExportLibrary(InteropLibrary.class)
public final class FunctionObject implements TruffleObject {
    public static FunctionObject create(CallTarget callTarget, int argumentCount,
            Object methodTarget) {
        return new FunctionObject(callTarget, argumentCount, methodTarget);
    }

    public final CallTarget callTarget;
    public final int argumentCount;
    public final Object methodTarget;
    private final FunctionDispatchNode functionDispatchNode;

    public FunctionObject(CallTarget callTarget, int argumentCount) {
        this(callTarget, argumentCount, null);
    }

    private FunctionObject(CallTarget callTarget, int argumentCount,
            Object methodTarget) {
        this.callTarget = callTarget;
        this.argumentCount = argumentCount;
        this.methodTarget = methodTarget;
        this.functionDispatchNode = FunctionDispatchNodeGen.create();
    }

    /**
     * Returns the string representation of a given function.
     * In JavaScript, this returns the actual code of a given function (!).
     * We'll simplify in EasyScript, and just return the string {@code "[Function]"}.
     */
    @Override
    public String toString() {
        return "[object Function]";
    }

    @ExportMessage
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    Object execute(Object[] arguments) {
        var extendedArguments = new Object[arguments.length + 1];
        extendedArguments[0] = Undefined.INSTANCE;
        for (int i = 0; i < arguments.length; i++) {
            var argument = arguments[i];
            // we have to make sure the given arguments are valid EasyScript values,
            // as this class can be invoked from other languages, like Java
            if (!this.isEasyScriptValue(argument)) {
                throw new EasyScriptException("'" + argument + "' is not an EasyScript value");
            } else {
                extendedArguments[i + 1] = argument;
            }
        }
        return this.functionDispatchNode.executeDispatch(this, extendedArguments);
    }

    private boolean isEasyScriptValue(Object argument) {
        // as of this chapter, the only available types in EasyScript are
        // numbers (ints and doubles), booleans, 'undefined', and functions
        return EasyScriptTypeSystemGen.isImplicitDouble(argument) ||
                EasyScriptTypeSystemGen.isBoolean(argument) ||
                argument == Undefined.INSTANCE ||
                argument instanceof ArrayObject ||
                argument instanceof TruffleString ||
                argument instanceof String ||
                argument instanceof FunctionObject;
    }
}
