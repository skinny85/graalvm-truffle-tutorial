package com.endoflineblog.truffle.part_06;

import com.endoflineblog.truffle.part_06.runtime.GlobalScopeObject;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Identical to the class with the same name from part 5.
 */
public final class EasyScriptLanguageContext {
    public final GlobalScopeObject globalScopeObject;

    public EasyScriptLanguageContext() {
        this.globalScopeObject = new GlobalScopeObject();
    }
}
