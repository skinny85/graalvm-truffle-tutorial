package com.endoflineblog.truffle.part_13;

import com.endoflineblog.truffle.part_13.common.ShapesAndPrototypes;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Identical to the class with the same name from part 11.
 */
public final class EasyScriptLanguageContext {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    /** Retrieve the current language context for the given {@link Node}. */
    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }

    public final DynamicObject globalScopeObject;
    public final ShapesAndPrototypes shapesAndPrototypes;

    public EasyScriptLanguageContext(DynamicObject globalScopeObject, ShapesAndPrototypes shapesAndPrototypes) {
        this.globalScopeObject = globalScopeObject;
        this.shapesAndPrototypes = shapesAndPrototypes;
    }
}
