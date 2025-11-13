package com.endoflineblog.truffle.part_16.runtime;

import com.endoflineblog.truffle.part_16.EasyScriptTypeSystemGen;
import com.endoflineblog.truffle.part_16.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_16.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_16.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The object that represents a function in EasyScript.
 * Identical to the class with the same name from part 15.
 */
@ExportLibrary(InteropLibrary.class)
public final class FunctionObject extends JavaScriptObject {
    public final CallTarget callTarget;
    public final int argumentCount;
    public final MaterializedFrame materializedFrame;

    private final FunctionDispatchNode functionDispatchNode;

    public FunctionObject(Shape shape, ClassPrototypeObject functionPrototype, CallTarget callTarget, int argumentCount) {
        this(shape, functionPrototype, callTarget, argumentCount, null);
    }

    public FunctionObject(Shape shape, ClassPrototypeObject functionPrototype, CallTarget callTarget, int argumentCount, MaterializedFrame materializedFrame) {
        super(shape, functionPrototype);

        this.callTarget = callTarget;
        this.argumentCount = argumentCount;
        this.materializedFrame = materializedFrame;
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
