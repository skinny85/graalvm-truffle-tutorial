package com.endoflineblog.truffle.part_06.runtime;

import com.endoflineblog.truffle.part_06.EasyScriptException;
import com.endoflineblog.truffle.part_06.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_06.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * The object that represents a function in EasyScript.
 */
@ExportLibrary(InteropLibrary.class)
public final class FunctionObject implements TruffleObject {
    public final CallTarget callTarget;
    private final FunctionDispatchNode functionDispatchNode;

    public FunctionObject(CallTarget callTarget) {
        this.callTarget = callTarget;
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
        // numbers (ints and doubles), 'undefined', and functions
        return EasyScriptTypeSystemGen.isImplicitDouble(argument) ||
                argument == Undefined.INSTANCE ||
                argument instanceof FunctionObject;
    }
}
