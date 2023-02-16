package com.endoflineblog.truffle.part_11.nodes.exprs.variables;

import com.endoflineblog.truffle.part_11.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.GlobalScopeObjectExprNode;
import com.endoflineblog.truffle.part_11.nodes.stmts.variables.GlobalVarDeclStmtNode;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Property;

/**
 * A Node that represents the expression of referencing a global variable in EasyScript.
 * Similar to the class with the same name from part 9,
 * the main difference is that we read the value of the variable directly from the
 * {@link com.endoflineblog.truffle.part_11.runtime.GlobalScopeObject}
 * (for getting a reference to which we use the {@link GlobalScopeObjectExprNode}),
 * using {@link DynamicObjectLibrary}.
 */
@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarReferenceExprNode extends EasyScriptExprNode {
    protected abstract String getName();

    @Specialization(limit = "1", guards = "isInt(property)")
    protected int readIntVariable(DynamicObject globalScopeObject,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary,
            @Cached("objectLibrary.getProperty(globalScopeObject, getName())") Property property) {
        try {
            return objectLibrary.getIntOrDefault(globalScopeObject, this.getName(), 0);
        } catch (UnexpectedResultException e) {
            throw new EasyScriptException(e.getMessage());
        }
    }

    @Specialization(limit = "1", guards = "isDouble(property)", replaces = "readIntVariable")
    protected double readDoubleVariable(DynamicObject globalScopeObject,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary,
            @Cached("objectLibrary.getProperty(globalScopeObject, getName())") Property property) {
        try {
            return objectLibrary.getDoubleOrDefault(globalScopeObject, this.getName(), 0.0);
        } catch (UnexpectedResultException e) {
            throw new EasyScriptException(e.getMessage());
        }
    }

    @Specialization(limit = "1",  replaces = {"readIntVariable", "readDoubleVariable"})
    protected Object readObjectVariable(DynamicObject globalScopeObject,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        String variableId = this.getName();
        var value = objectLibrary.getOrDefault(globalScopeObject, variableId, null);
        if (value == null) {
            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
        } else if (value == GlobalVarDeclStmtNode.DUMMY) {
            throw new EasyScriptException("Cannot access '" + variableId + "' before initialization");
        } else {
            return value;
        }
    }

    protected boolean isInt(Property property) {
        return checkFlag(property, GlobalVarDeclStmtNode.INT_TYPE_FLAG);
    }

    protected boolean isDouble(Property property) {
        return checkFlag(property, GlobalVarDeclStmtNode.DOUBLE_TYPE_FLAG);
    }

    private static boolean checkFlag(Property property, int typeFlag) {
        return property != null &&
                (property.getFlags() & GlobalVarDeclStmtNode.OBJECT_TYPE_FLAG) == 0 &&
                (property.getFlags() & typeFlag) != 0;
    }
}
