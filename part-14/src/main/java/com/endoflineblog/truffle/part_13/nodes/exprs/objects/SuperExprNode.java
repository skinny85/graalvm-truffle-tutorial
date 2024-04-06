package com.endoflineblog.truffle.part_13.nodes.exprs.objects;

import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeChainObject;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The Node implementing the 'super' expression.
 */
public final class SuperExprNode extends EasyScriptExprNode {
    private final ClassPrototypeChainObject classPrototype;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ThisExprNode thisExprNode;

    public SuperExprNode(ClassPrototypeChainObject classPrototype) {
        this.classPrototype = classPrototype;
        this.thisExprNode = new ThisExprNode();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // executeGeneric() simply returns 'this'
        // (that will be the method that property access Nodes use to establish the method call receiver,
        // in their evaluateAsReceiver() methods)
        return this.thisExprNode.executeGeneric(frame);
    }

    public Object readParentPrototype() {
        // this method is called from the property access Nodes
        // to find the parent prototype
        return this.classPrototype.superClassPrototype;
    }
}
