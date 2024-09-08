package com.endoflineblog.truffle.part_15.nodes.exprs.objects;

import com.endoflineblog.truffle.part_15.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_15.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_15.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_15.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.endoflineblog.truffle.part_15.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_15.runtime.FunctionObject;
import com.endoflineblog.truffle.part_15.runtime.JavaScriptObject;
import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * The Node for handling {@code new} expressions.
 * Identical to the class with the same name from part 14.
 */
public abstract class NewExprNode extends EasyScriptExprNode {
    @Child
    @Executed
    protected EasyScriptExprNode constructorExpr;

    @Children
    private final EasyScriptExprNode[] args;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private FunctionDispatchNode constructorDispatchNode;

    protected NewExprNode(EasyScriptExprNode constructorExpr, List<EasyScriptExprNode> args) {
        this.constructorExpr = constructorExpr;
        this.args = args.toArray(EasyScriptExprNode[]::new);
        this.constructorDispatchNode = FunctionDispatchNodeGen.create();
    }

    /**
     * The specialization for when the constructor expression evaluates to an
     * {@link ClassPrototypeObject}.
     */
    @Specialization(limit = "2")
    protected Object instantiateObject(VirtualFrame frame, ClassPrototypeObject classPrototypeObject,
            @CachedLibrary("classPrototypeObject") InteropLibrary interopPrototypeLibrary) {
        var object = new JavaScriptObject(this.currentLanguageContext().shapesAndPrototypes.rootShape, classPrototypeObject);
        Object constructor = null;
        try {
            constructor = interopPrototypeLibrary.readMember(classPrototypeObject, "constructor");
        } catch (UnknownIdentifierException e) {
            // fall through to below
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
        if (constructor instanceof FunctionObject) {
            // instanceof always returns 'false' for 'null'
            Object[] args = this.executeArguments(frame);
            var boundConstructor = (FunctionObject) constructor;
            this.constructorDispatchNode.executeDispatch(boundConstructor, args, object);
        } else {
            this.consumeArguments(frame);
        }
        return object;
    }

    /**
     * The specialization for when the constructor expression evaluates to something other than
     * {@link ClassPrototypeObject}.
     */
    @Fallback
    protected Object instantiateNonConstructor(VirtualFrame frame, Object object) {
        this.consumeArguments(frame);
        throw new EasyScriptException("'" + object + "' is not a constructor");
    }

    /**
     * Even though we don't use the arguments for anything in this part of the series,
     * we need to evaluate them, as they can have side effects
     * (like assignment).
     */
    @ExplodeLoop
    private void consumeArguments(VirtualFrame frame) {
        for (int i = 0; i < this.args.length; i++) {
            this.args[i].executeGeneric(frame);
        }
    }

    @ExplodeLoop
    private Object[] executeArguments(VirtualFrame frame) {
        var args = new Object[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            args[i] = this.args[i].executeGeneric(frame);
        }
        return args;
    }
}
