package com.endoflineblog.truffle.part_05;

import com.endoflineblog.truffle.part_05.nodes.EasyScriptRootNode;
import com.endoflineblog.truffle.part_05.nodes.stmts.EasyScriptStmtNode;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;

import java.util.List;

/**
 * The EasyScript Graal polyglot language implementation.
 * Very similar to EasyScriptTruffleLanguage in part 4,
 * the main difference is that we now need a context class,
 * because that's where we store the object containing our global variables.
 *
 * @see #createContext
 * @see #getScope
 */
@TruffleLanguage.Registration(id = "ezs", name = "EasyScript")
public final class EasyScriptTruffleLanguage extends TruffleLanguage<EasyScriptLanguageContext> {
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        List<EasyScriptStmtNode> stmts = EasyScriptTruffleParser.parse(request.getSource().getReader());
        var rootNode = new EasyScriptRootNode(this, stmts);
        return rootNode.getCallTarget();
    }

    /** We return the context that contains the global scope object. */
    @Override
    protected EasyScriptLanguageContext createContext(Env env) {
        return new EasyScriptLanguageContext();
    }

    /** The top-level scope is kept as a field of our context class.  */
    @Override
    protected Object getScope(EasyScriptLanguageContext context) {
        return context.globalScopeObject;
    }
}
