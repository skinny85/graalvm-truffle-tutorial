package com.endoflineblog.truffle.part_10.runtime;

import com.endoflineblog.truffle.part_10.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_10.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_10.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_10.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

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
    public final CallTarget callTarget;
    public final int argumentCount;
    private final FunctionDispatchNode functionDispatchNode;

    public FunctionObject(CallTarget callTarget, int argumentCount) {
        this.callTarget = callTarget;
        this.argumentCount = argumentCount;
        this.functionDispatchNode = FunctionDispatchNodeGen.create();
    }

    @ExportMessage
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    Object execute(Object[] arguments) {
        // we have to make sure the given arguments are valid EasyScript values,
        // as this class can be invoked from other languages, like Java
        for (Object argument : arguments) {
            if (!this.isEasyScriptValue(argument)) {
                throw new EasyScriptException("'" + argument + "' is not an EasyScript value");
            }
        }
        return this.functionDispatchNode.executeDispatch(this, arguments);
    }

    private boolean isEasyScriptValue(Object argument) {
        // as of this chapter, the only available types in EasyScript are
        // numbers (ints and doubles), booleans, 'undefined', functions, and arrays
        return EasyScriptTypeSystemGen.isImplicitDouble(argument) ||
                EasyScriptTypeSystemGen.isBoolean(argument) ||
                argument == Undefined.INSTANCE ||
                argument instanceof ArrayObject ||
                argument instanceof FunctionObject;
    }
}
