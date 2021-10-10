package com.endoflineblog.truffle.part_06;

import com.endoflineblog.truffle.part_06.runtime.GlobalScopeObject;

public final class EasyScriptLanguageContext {
    public final GlobalScopeObject globalScopeObject;

    public EasyScriptLanguageContext() {
        this.globalScopeObject = new GlobalScopeObject();
    }
}
