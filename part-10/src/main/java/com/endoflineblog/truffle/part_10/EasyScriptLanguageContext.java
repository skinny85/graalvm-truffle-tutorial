package com.endoflineblog.truffle.part_10;

import com.endoflineblog.truffle.part_10.runtime.GlobalScopeObject;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Almost identical to the class with the same name from part 9,
 * the only difference is that we get a {@link Shape}
 * in our constructor from {@link EasyScriptTruffleLanguage},
 * as the {@link GlobalScopeObject} is now a {@link DynamicObject},
 * and requires one when being created.
 */
public final class EasyScriptLanguageContext {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    /** Retrieve the current language context for the given {@link Node}. */
    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }

    public final DynamicObject globalScopeObject;

    public EasyScriptLanguageContext(Shape globalScopeShape) {
        this.globalScopeObject = new GlobalScopeObject(globalScopeShape);
    }
}
