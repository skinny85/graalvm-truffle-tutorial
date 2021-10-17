package com.endoflineblog.truffle.part_06;

import com.endoflineblog.truffle.part_06.nodes.FunctionRootNode;
import com.endoflineblog.truffle.part_06.nodes.ProgramRootNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.ReadFunctionArgExprNode;
import com.endoflineblog.truffle.part_06.nodes.exprs.functions.AbsFunctionBodyExprNodeGen;
import com.endoflineblog.truffle.part_06.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_06.runtime.FunctionObject;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;

import java.util.List;

@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<EasyScriptLanguageContext> {
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        List<EasyScriptStmtNode> stmts = EasyScriptTruffleParser.parse(request.getSource().getReader());
        var rootNode = new ProgramRootNode(this, stmts);
        return Truffle.getRuntime().createCallTarget(rootNode);
    }

    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        var context = new EasyScriptLanguageContext();

        context.globalScopeObject.newConstant("Math.abs",
                new FunctionObject(Truffle.getRuntime().createCallTarget(new FunctionRootNode(this,
                        AbsFunctionBodyExprNodeGen.create(new ReadFunctionArgExprNode(0))))));

        return context;
    }

    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }
}
