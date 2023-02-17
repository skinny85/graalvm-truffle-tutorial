package com.endoflineblog.truffle.part_11.nodes.exprs.variables;

import com.endoflineblog.truffle.part_11.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_11.nodes.exprs.GlobalScopeObjectExprNode;
import com.endoflineblog.truffle.part_11.nodes.stmts.variables.GlobalVarDeclStmtNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

/**
 * A Node that represents the expression of assigning a value to a global variable in EasyScript.
 * Similar to the class with the same name from part 9,
 * the main difference is that we save the value of the variable
 * directly in the {@link com.endoflineblog.truffle.part_11.runtime.GlobalScopeObject}
 * (for getting a reference to which we use the {@link GlobalScopeObjectExprNode}),
 * using {@link DynamicObjectLibrary}.
 */
@NodeChild(value = "globalScopeObjectExpr", type = GlobalScopeObjectExprNode.class)
@NodeChild(value = "assignmentExpr")
@NodeField(name = "name", type = String.class)
public abstract class GlobalVarAssignmentExprNode extends EasyScriptExprNode {
    protected abstract String getName();

    @Specialization(limit = "1", guards = "isInt(objectLibrary, globalScopeObject, getName())")
    protected int assignIntVariable(DynamicObject globalScopeObject, int value,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        objectLibrary.putInt(globalScopeObject, this.getName(), value);
//        int constFlag = property.getFlags() & GlobalVarDeclStmtNode.CONST_FLAG;
        objectLibrary.setPropertyFlags(globalScopeObject, this.getName(),
                GlobalVarDeclStmtNode.INT_TYPE_FLAG);
        return value;
    }

    @Specialization(limit = "1", guards = "isDouble(objectLibrary, globalScopeObject, getName())",
            replaces = "assignIntVariable")
    protected double assignDoubleVariable(DynamicObject globalScopeObject, double value,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        objectLibrary.putDouble(globalScopeObject, this.getName(), value);
//        int constFlag = property.getFlags() & GlobalVarDeclStmtNode.CONST_FLAG;
        objectLibrary.setPropertyFlags(globalScopeObject, this.getName(),
                GlobalVarDeclStmtNode.DOUBLE_TYPE_FLAG);
        return value;
    }

    @Specialization(limit = "1", replaces = {"assignIntVariable", "assignDoubleVariable"})
    protected Object assignObjectVariable(DynamicObject globalScopeObject, Object value,
            @CachedLibrary("globalScopeObject") DynamicObjectLibrary objectLibrary) {
        String variableId = this.getName();
//        Property property = objectLibrary.getProperty(globalScopeObject, variableId);
//        if (property == null) {
//            throw new EasyScriptException(this, "'" + variableId + "' is not defined");
//        }
//        if ((property.getFlags() & GlobalVarDeclStmtNode.CONST_FLAG) != 0) {
//            // this is a constant
//            Object existingValue = property.get(globalScopeObject, true);
//            if (existingValue != GlobalVarDeclStmtNode.DUMMY) {
//                // the first assignment to a constant is fine
//                throw new EasyScriptException("Assignment to constant variable '" + variableId + "'");
//            }
//        }
        objectLibrary.put(globalScopeObject, variableId, value);
//        int constFlag = property.getFlags() & GlobalVarDeclStmtNode.CONST_FLAG;
        objectLibrary.setPropertyFlags(globalScopeObject, this.getName(),
                GlobalVarDeclStmtNode.OBJECT_TYPE_FLAG);
        return value;
    }

    protected boolean isInt(DynamicObjectLibrary objectLibrary, DynamicObject globalScopeObject,
            String name) {
        return (objectLibrary.getPropertyFlagsOrDefault(globalScopeObject, name, 0) &
                GlobalVarDeclStmtNode.INT_TYPE_FLAG) != 0;
    }

    protected boolean isDouble(DynamicObjectLibrary objectLibrary, DynamicObject globalScopeObject,
            String name) {
        return (objectLibrary.getPropertyFlagsOrDefault(globalScopeObject, name, 0) &
                GlobalVarDeclStmtNode.DOUBLE_TYPE_FLAG) != 0;
    }
}
