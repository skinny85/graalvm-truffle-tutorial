package com.endoflineblog.truffle.part_13.nodes.exprs.variables;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.GlobalScopeObjectExprNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A Node that represents the expression of referencing a global variable in EasyScript.
 * Identical to the class with the same name from part 12.
 */
@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarReferenceExprNode extends EasyScriptExprNode {
    protected abstract String getName();

    @Specialization(limit = "2")
    protected Object readVariable(DynamicObject globalScopeObject,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        String variableId = this.getName();
        var value = objectLibrary.getOrDefault(globalScopeObject, variableId, null);
        if (value == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        } else {
            return value;
        }
    }
}
