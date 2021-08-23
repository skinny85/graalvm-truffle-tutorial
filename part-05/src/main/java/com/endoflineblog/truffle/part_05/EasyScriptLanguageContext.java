package com.endoflineblog.truffle.part_05;

import com.endoflineblog.truffle.part_05.runtime.GlobalScopeObject;

public final class EasyScriptLanguageContext {
    public final GlobalScopeObject globalScopeObject;

    public EasyScriptLanguageContext() {
        this.globalScopeObject = new GlobalScopeObject();
    }
}
