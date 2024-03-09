package com.endoflineblog.truffle.part_13.nodes.exprs.objects;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.functions.FunctionDispatchNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.functions.FunctionDispatchNodeGen;
import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_13.runtime.FunctionObject;
import com.endoflineblog.truffle.part_13.runtime.JavaScriptObject;
import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

import java.util.List;

/**
 * The Node for handling {@code new} expressions.
 * Very similar to the class with the same name from part 12,
 * the only difference is adding support for invoking the constructor of the class
 * if one has been defined.
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
     * The specialization for when the constructor expression evaluates to a
     * {@link ClassPrototypeObject}.
     */
    @Specialization(limit = "2")
    protected Object instantiateObject(VirtualFrame frame, ClassPrototypeObject classPrototypeObject,
            @CachedLibrary("classPrototypeObject") DynamicObjectLibrary dynamicObjectLibrary) {
        var object = new JavaScriptObject(this.currentLanguageContext().shapesAndPrototypes.rootShape, classPrototypeObject);
        var constructor = dynamicObjectLibrary.getOrDefault(classPrototypeObject, "constructor", null);
        if (constructor instanceof FunctionObject) {
            // instanceof always returns 'false' for 'null'
            var args = this.executeArguments(frame);
            FunctionObject boundConstructor = (FunctionObject) constructor;
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
