package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.EasyScriptException;
import com.endoflineblog.truffle.part_07.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_07.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_07.nodes.UserDefinedFuncRootNode;
import com.endoflineblog.truffle.part_07.runtime.FunctionObject;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the declaration of a variable or constant in EasyScript.
 * Identical to the class with the same name from part 5.
 */
//@NodeChild(value = "initializerExpr", type = EasyScriptExprNode.class)
//@NodeField(name = "name", type = String.class)
public final class FuncDeclStmtNode extends EasyScriptStmtNode {
    private final String funcName;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private BlockStmtNode funcBody;

    public FuncDeclStmtNode(String funcName, BlockStmtNode funcBody) {
        this.funcName = funcName;
        this.funcBody = funcBody;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        EasyScriptTruffleLanguage truffleLanguage = EasyScriptTruffleLanguage.get(this);
        UserDefinedFuncRootNode funcRootNode = new UserDefinedFuncRootNode(truffleLanguage, this.funcBody);
        FunctionObject func = new FunctionObject(funcRootNode.getCallTarget());

        EasyScriptLanguageContext context = EasyScriptLanguageContext.get(this);
        if (!context.globalScopeObject.newConstant(this.funcName, func)) {
            throw new EasyScriptException(this, "Identifier '" + this.funcName + "' has already been declared");
        }

        // we return 'undefined' for statements that declare functions
        return Undefined.INSTANCE;
    }
}
