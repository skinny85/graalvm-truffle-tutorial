package com.endoflineblog.truffle.part_15.nodes.exprs;

import com.endoflineblog.truffle.part_15.nodes.stmts.variables.FuncDeclStmtNode;
import com.endoflineblog.truffle.part_15.runtime.ClassPrototypeObject;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;

/**
 * A simple expression Node that just returns the given {@link DynamicObject}.
 * Used for handling methods inside class declarations,
 * by passing an instance of {@link ClassPrototypeObject}
 * to the constructor of this class,
 * and then passing the instance created from that constructor to
 * {@link FuncDeclStmtNode}.
 * Identical to the class with the same name from part 14.
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
