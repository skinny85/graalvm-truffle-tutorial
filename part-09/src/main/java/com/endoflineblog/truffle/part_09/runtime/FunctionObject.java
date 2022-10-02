package com.endoflineblog.truffle.part_09.runtime;

import com.endoflineblog.truffle.part_09.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_09.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_09.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_09.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.utilities.CyclicAssumption;

/**
 * The object that represents a function in EasyScript.
 * Almost identical to the class with the same name from part 6,
 * except we save the number of arguments the function takes.
 *
 * @see #argumentCount
 */
@ExportLibrary(InteropLibrary.class)
public final class FunctionObject implements TruffleObject {
    private final String functionName;
    private final CyclicAssumption functionWasNotRedefinedCyclicAssumption;
    private final FunctionDispatchNode functionDispatchNode;

    private CallTarget callTarget;

    /**
     * The number of declared arguments this function takes.
     * We check that from the {@link FunctionDispatchNode}
     * so we can extend the arguments with 'undefined's
     * if the function is called with less of them than this number.
     */
    private int argumentCount;


    public FunctionObject(String functionName, CallTarget callTarget, int argumentCount) {
        this.functionName = functionName;
        this.functionWasNotRedefinedCyclicAssumption = new CyclicAssumption(this.functionName);
        this.functionDispatchNode = FunctionDispatchNodeGen.create();
        this.callTarget = callTarget;
        this.argumentCount = argumentCount;
    }

    public void redefine(CallTarget callTarget, int argumentCount) {
        if (this.callTarget != callTarget) {
            this.callTarget = callTarget;
            this.argumentCount = argumentCount;
            this.functionWasNotRedefinedCyclicAssumption.invalidate("Function '" + this.functionName + "' was redefined");
        }
    }

    public Assumption getFunctionWasNotRedefinedAssumption() {
        return this.functionWasNotRedefinedCyclicAssumption.getAssumption();
    }

    public CallTarget getCallTarget() {
        return this.callTarget;
    }

    public int getArgumentCount() {
        return this.argumentCount;
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
