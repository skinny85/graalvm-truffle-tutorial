package com.endoflineblog.truffle.part_05;

import com.endoflineblog.truffle.part_05.runtime.GlobalScopeObject;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Includes the scope that contains EasyScript global variables.
 *
 * @see #globalScopeObject
 */
public final class EasyScriptLanguageContext {
    /** The object that stores EasyScript global variables. */
    public final GlobalScopeObject globalScopeObject;

    public EasyScriptLanguageContext() {
        this.globalScopeObject = new GlobalScopeObject();
    }
}
