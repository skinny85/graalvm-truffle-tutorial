package com.endoflineblog.truffle.part_12.runtime;

import com.endoflineblog.truffle.part_12.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The object that represents a function in EasyScript.
 * Almost identical to the class with the same name from part 11,
 * the only difference is adding {@link ClassInstanceObject}
 * to the list of allowed EasyScript values when calling this function through the GraalVM polyglot API.
 */
@ExportLibrary(InteropLibrary.class)
public final class FunctionObject implements TruffleObject {
    public final CallTarget callTarget;
    public final int argumentCount;

    /**
     * The target of the method represented by this object.
     * If this object represents a function,
     * this will be {@code null}.
     * If this object represents a method,
     * this field will store the object that method was invoked on
     * (in this part of the series, that will always be a {@link TruffleString},
     * since we don't support methods that can refer to {@code this}
     * in user-defined classes yet).
     * This field is read by the {@link FunctionDispatchNode function dispatch Node},
     * and by the Node that reads properties of {@link TruffleString}s.
     *
     * @see FunctionDispatchNode
     * @see com.endoflineblog.truffle.part_12.nodes.exprs.strings.ReadTruffleStringPropertyNode
     */
    public final Object methodTarget;

    private final FunctionDispatchNode functionDispatchNode;

    public FunctionObject(CallTarget callTarget, int argumentCount) {
        this(callTarget, argumentCount, null);
    }

    public FunctionObject(CallTarget callTarget, int argumentCount,
            Object methodTarget) {
        this.callTarget = callTarget;
        this.argumentCount = argumentCount;
        this.methodTarget = methodTarget;
        this.functionDispatchNode = FunctionDispatchNodeGen.create();
    }

    /**
     * Returns the string representation of a given function.
     * In JavaScript, this returns the actual code of a given function (!).
     * We'll simplify in EasyScript, and just return the string {@code "[object Function]"}.
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
        // numbers (ints and doubles), booleans, 'undefined', functions, and strings
        return EasyScriptTypeSystemGen.isImplicitDouble(argument) ||
                EasyScriptTypeSystemGen.isBoolean(argument) ||
                argument == Undefined.INSTANCE ||
                argument instanceof ArrayObject ||
                argument instanceof TruffleString ||
                argument instanceof String ||
                argument instanceof ClassInstanceObject ||
                argument instanceof FunctionObject;
    }
}
