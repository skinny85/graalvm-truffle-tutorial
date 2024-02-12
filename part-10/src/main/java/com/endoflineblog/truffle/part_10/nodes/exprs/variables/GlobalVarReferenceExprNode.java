package com.endoflineblog.truffle.part_10.nodes.exprs.variables;

import com.endoflineblog.truffle.part_10.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_10.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_10.nodes.exprs.GlobalScopeObjectExprNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A Node that represents the expression of referencing a global variable in EasyScript.
 * Similar to the class with the same name from part 9,
 * the main difference is that we read the value of the variable directly from the
 * {@link com.endoflineblog.truffle.part_10.runtime.GlobalScopeObject}
 * (for getting a reference to which we use the {@link GlobalScopeObjectExprNode}),
 * using {@link DynamicObjectLibrary}.
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
