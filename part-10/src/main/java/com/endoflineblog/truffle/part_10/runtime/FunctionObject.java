package com.endoflineblog.truffle.part_10.runtime;

import com.endoflineblog.truffle.part_10.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_10.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_10.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_10.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.utilities.CyclicAssumption;

/**
 * The object that represents a function in EasyScript.
 * The big difference between this, and the class with the same name from part 8,
 * is that this class is mutable -
 * the {@link CallTarget} it wraps can be changed using the
 * {@link #redefine} method.
 * We add an {@link Assumption} that tracks whether a function was redefined,
 * and we check that assumption in {@link FunctionDispatchNode}.
 *
 * @see #redefine
 * @see #getFunctionWasNotRedefinedAssumption
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

    /**
     * Change the {@link CallTarget} this {@link FunctionObject} wraps,
     * if it's different than the one currently being wrapped.
     * Invalidates the last {@link Assumption} returned by
     * {@link #getFunctionWasNotRedefinedAssumption()},
     * and creates a new one that will now be returned from that method.
     */
    public void redefine(CallTarget callTarget, int argumentCount) {
        if (this.callTarget != callTarget) {
            this.callTarget = callTarget;
            this.argumentCount = argumentCount;
            this.functionWasNotRedefinedCyclicAssumption.invalidate("Function '" + this.functionName + "' was redefined");
        }
    }

    /**
     * Return an Assumption that becomes invalidated after {@link #redefine}
     * is called (for a different {@link CallTarget} than the current one)
     * after this method has been.
     */
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
