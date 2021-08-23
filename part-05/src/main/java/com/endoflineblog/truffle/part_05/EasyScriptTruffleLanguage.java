package com.endoflineblog.truffle.part_05;

import com.endoflineblog.truffle.part_05.nodes.EasyScriptRootNode;
import com.endoflineblog.truffle.part_05.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;

import java.util.List;

/**
 * The EasyScript Graal polyglot language implementation.
 * Very similar to EasyScriptTruffleLanguage in part 4.
 */
@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<EasyScriptLanguageContext> {
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        var easyScriptTruffleParser = new EasyScriptTruffleParser();
        List<EasyScriptStmtNode> stmts = easyScriptTruffleParser.parse(request.getSource().getReader());
        var rootNode = new EasyScriptRootNode(this, stmts);
        return Truffle.getRuntime().createCallTarget(rootNode);
    }

    /**
     * We still don't need a Context,
     * so we still return {@code null} here.
     */
    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        return new EasyScriptLanguageContext();
    }

    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }
}
