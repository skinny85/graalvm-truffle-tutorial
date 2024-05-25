package com.endoflineblog.truffle.part_14;

import com.endoflineblog.truffle.part_14.common.ShapesAndPrototypes;
import com.endoflineblog.truffle.part_14.runtime.FunctionObject;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Very similar to the class with the same name from part 13,
 * the only difference is the new {@link #emptyFunction} field used by
 * {@link com.endoflineblog.truffle.part_14.nodes.exprs.objects.SuperExprNode}
 * in case {@code super()} references a class without a constructor defined.
 *
 * @see #emptyFunction
 */
public final class EasyScriptLanguageContext {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    /** Retrieve the current language context for the given {@link Node}. */
    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }

    public final DynamicObject globalScopeObject;

    /**
     * The object containing the shapes and prototypes,
     * both for user-defined class instances, and for built-in objects.
     */
    public final ShapesAndPrototypes shapesAndPrototypes;

    public final FunctionObject emptyFunction;

    public EasyScriptLanguageContext(DynamicObject globalScopeObject,
            ShapesAndPrototypes shapesAndPrototypes, FunctionObject emptyFunction) {
        this.globalScopeObject = globalScopeObject;
        this.shapesAndPrototypes = shapesAndPrototypes;
        this.emptyFunction = emptyFunction;
    }
}
