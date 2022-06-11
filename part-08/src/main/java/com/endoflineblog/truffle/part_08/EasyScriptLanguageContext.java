package com.endoflineblog.truffle.part_08;

import com.endoflineblog.truffle.part_08.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_08.runtime.GlobalScopeObject;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Almost identical to the class with the same name from part 6,
 * the only difference is the {@link #get} method backed by the
 * {@link TruffleLanguage.ContextReference} field,
 * and used in the {@link EasyScriptNode#currentLanguageContext()} method.
 *
 * @see #get
 */
public final class EasyScriptLanguageContext {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    /** Retrieve the current language context for the given {@link Node}. */
    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }

    public final GlobalScopeObject globalScopeObject;

    public EasyScriptLanguageContext() {
        this.globalScopeObject = new GlobalScopeObject();
    }
}
