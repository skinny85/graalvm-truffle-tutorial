package com.endoflineblog.truffle.part_06;

import com.endoflineblog.truffle.part_05.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_06.nodes.EasyScriptRootNode;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;

import java.util.List;

/**
 * The EasyScript Graal polyglot language implementation.
 * Very similar to EasyScriptTruffleLanguage in part 4.
 */
@TruffleLanguage.Registration(id = "ezs2", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<EasyScriptLanguageContext> {
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        List<EasyScriptStmtNode> stmts = EasyScriptTruffleParser.parse(request.getSource().getReader());
        var rootNode = new EasyScriptRootNode(this, stmts);
        return Truffle.getRuntime().createCallTarget(rootNode);
    }

    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        return new EasyScriptLanguageContext();
    }

    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }
}
