package com.endoflineblog.truffle.part_05;

import com.endoflineblog.truffle.part_05.runtime.GlobalScopeObject;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 */
public final class EasyScriptLanguageContext {
    public final GlobalScopeObject globalScopeObject;

    public EasyScriptLanguageContext() {
        this.globalScopeObject = new GlobalScopeObject();
    }
}
