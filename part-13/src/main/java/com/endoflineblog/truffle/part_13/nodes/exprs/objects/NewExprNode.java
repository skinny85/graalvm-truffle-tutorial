package com.endoflineblog.truffle.part_13.nodes.exprs.objects;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.runtime.ClassInstanceObject;
import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject;
import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

/**
 * The Node for handling {@code new} expressions.
 */
public abstract class NewExprNode extends EasyScriptExprNode {
    @Child
    @Executed
    protected EasyScriptExprNode constructorExpr;

    @Children
    private final EasyScriptExprNode[] args;

    protected NewExprNode(EasyScriptExprNode constructorExpr, List<EasyScriptExprNode> args) {
        this.constructorExpr = constructorExpr;
        this.args = args.toArray(EasyScriptExprNode[]::new);
    }

    /**
     * The specialization for when the constructor expression evaluates to a
     * {@link ClassPrototypeObject}.
     */
    @Specialization
    protected Object instantiateObject(VirtualFrame frame, ClassPrototypeObject classPrototypeObject) {
        this.consumeArguments(frame);
        return new ClassInstanceObject(classPrototypeObject);
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
}
