package com.endoflineblog.truffle.part_13.nodes.exprs.objects;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;

/**
 * The Node implementing the 'super' expression.
 */
public final class SuperExprNode extends EasyScriptExprNode {
    private final ClassPrototypeObject classPrototype;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ThisExprNode thisExprNode;

    @Child
    private InteropLibrary interopLibrary;

    public SuperExprNode(ClassPrototypeObject classPrototype) {
        this.classPrototype = classPrototype;
        this.thisExprNode = new ThisExprNode();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return this.classPrototype.superClassPrototype;
    }

    @Override
    public Object evaluateAsTarget(VirtualFrame frame) {
        return this.executeGeneric(frame);
    }

    @Override
    public Object evaluateAsFunction(VirtualFrame frame, Object target) {
        if (this.interopLibrary == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.interopLibrary = this.insert(
                    InteropLibrary.getFactory().createDispatched(1));
        }
        try {
            return this.interopLibrary.readMember(target, "constructor");
        } catch (UnknownIdentifierException e) {
            return this.currentLanguageContext().emptyFunction;
        } catch (UnsupportedMessageException e) {
            throw new EasyScriptException(this, e.getMessage());
        }
    }

    @Override
    public Object evaluateAsThis(VirtualFrame frame, Object target) {
        return this.thisExprNode.executeGeneric(frame);
    }
}
