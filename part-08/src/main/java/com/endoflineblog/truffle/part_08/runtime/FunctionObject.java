package com.endoflineblog.truffle.part_08.runtime;

import com.endoflineblog.truffle.part_08.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_08.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_08.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * The object that represents a function in EasyScript.
 * Almost identical to the class with the same name from part 6,
 * except we save the number of arguments the function takes.
 *
 * @see #argumentCount
 */
@ExportLibrary(InteropLibrary.class)
public final class FunctionObject implements TruffleObject {
    public final CallTarget callTarget;

    /**
     * The number of declared arguments this function takes.
     * We check that from the {@link FunctionDispatchNode}
     * so we can extend the arguments with 'undefined's
     * if the function is called with less of them than this number.
     */
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
        // numbers (ints and doubles), booleans, 'undefined', and functions
        return EasyScriptTypeSystemGen.isImplicitDouble(argument) ||
                EasyScriptTypeSystemGen.isBoolean(argument) ||
                argument == Undefined.INSTANCE ||
                argument instanceof FunctionObject;
    }
}
