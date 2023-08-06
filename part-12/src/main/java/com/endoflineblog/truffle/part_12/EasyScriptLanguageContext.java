package com.endoflineblog.truffle.part_12;

import com.endoflineblog.truffle.part_12.nodes.exprs.strings.ReadTruffleStringPropertyExprNode;
import com.endoflineblog.truffle.part_12.runtime.GlobalScopeObject;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Almost identical to the class with the same name from part 10,
 * the only difference is that we get an instance of {@link StringPrototype}
 * in the constructor, and save it as a field.
 * That field is then read by the expression Node for reading string properties.
 *
 * @see #stringPrototype
 * @see ReadTruffleStringPropertyExprNode
 */
public final class EasyScriptLanguageContext {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    /** Retrieve the current language context for the given {@link Node}. */
    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }

    public final Shape objectShape;
    public final DynamicObject globalScopeObject;

    /**
     * The object containing the {@code CallTarget}s
     * for the built-in methods of strings.
     */
    public final JavaScriptObject stringPrototype;

    public EasyScriptLanguageContext(Shape objectShape, Shape globalScopeShape, JavaScriptObject stringPrototype) {
        this.objectShape = objectShape;
        this.globalScopeObject = new GlobalScopeObject(globalScopeShape);
        this.stringPrototype = stringPrototype;
    }
}
