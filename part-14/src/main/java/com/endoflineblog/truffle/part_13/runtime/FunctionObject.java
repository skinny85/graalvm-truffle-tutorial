package com.endoflineblog.truffle.part_13.runtime;

import com.endoflineblog.truffle.part_13.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_13.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The object that represents a function in EasyScript.
 * Very similar to the class with the same name from part 12,
 * the main differences are the removal of the {@code methodTarget} field,
 * since that has been moved into the {@link FunctionDispatchNode#executeDispatch} method,
 * and extending from {@link JavaScriptObject}
 * which contains the common logic of reading and writing properties.
 */
@ExportLibrary(InteropLibrary.class)
public final class FunctionObject extends JavaScriptObject {
    public final CallTarget callTarget;
    public final int argumentCount;

    private final FunctionDispatchNode functionDispatchNode;

    public FunctionObject(Shape shape, ClassPrototypeObject functionPrototype, CallTarget callTarget, int argumentCount) {
        super(shape, functionPrototype);

        this.callTarget = callTarget;
        this.argumentCount = argumentCount;
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
        // for GraalVM polyglot calls, we never fill the receiver
        return this.functionDispatchNode.executeDispatch(this, arguments, Undefined.INSTANCE);
    }

    private boolean isEasyScriptValue(Object argument) {
        // as of this chapter, the only available types in EasyScript are
        // numbers (ints and doubles), booleans, 'undefined', functions, and strings
        return EasyScriptTypeSystemGen.isImplicitDouble(argument) ||
                EasyScriptTypeSystemGen.isBoolean(argument) ||
                argument == Undefined.INSTANCE ||
                argument instanceof TruffleString ||
                argument instanceof String ||
                argument instanceof JavaScriptObject;
    }
}
