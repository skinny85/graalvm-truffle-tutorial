package com.endoflineblog.truffle.part_05;

import com.endoflineblog.truffle.part_05.runtime.GlobalScopeObject;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Includes the scope that contains EasyScript global variables,
 * and also the {@link #get} method that allows retrieving the current Truffle
 * language context for a given {@link Node},
 * used in the {@link com.endoflineblog.truffle.part_05.nodes.EasyScriptNode#currentLanguageContext()} method.
 *
 * @see #globalScopeObject
 * @see #get
 */
public final class EasyScriptLanguageContext {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    /** Retrieve the current language context for the given {@link Node}. */
    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }

    /** The object that stores EasyScript global variables. */
    public final GlobalScopeObject globalScopeObject;

    public EasyScriptLanguageContext() {
        this.globalScopeObject = new GlobalScopeObject();
    }
}
