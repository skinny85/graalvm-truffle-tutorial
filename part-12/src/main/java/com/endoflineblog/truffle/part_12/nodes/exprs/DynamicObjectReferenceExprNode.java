package com.endoflineblog.truffle.part_12.nodes.exprs;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;

/**
 * A simple expression Node that just returns the given {@link DynamicObject}.
 * Used for handling methods inside class declarations,
 * by passing an instance of {@link com.endoflineblog.truffle.part_12.runtime.ClassPrototypeObject}
 * to the constructor of this class,
 * and then passing the instance created from that constructor to
 * {@link com.endoflineblog.truffle.part_12.nodes.stmts.variables.FuncDeclStmtNode}.
 */
public final class DynamicObjectReferenceExprNode extends EasyScriptExprNode {
    private final DynamicObject dynamicObject;

    public DynamicObjectReferenceExprNode(DynamicObject dynamicObject) {
        this.dynamicObject = dynamicObject;
    }

    @Override
    public DynamicObject executeGeneric(VirtualFrame frame) {
        return this.dynamicObject;
    }
}
