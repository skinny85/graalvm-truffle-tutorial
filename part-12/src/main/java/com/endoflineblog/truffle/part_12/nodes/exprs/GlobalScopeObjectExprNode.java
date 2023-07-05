package com.endoflineblog.truffle.part_12.nodes.exprs;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.runtime.GlobalScopeObject;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;

/**
 * A simple expression class that returns the
 * {@link GlobalScopeObject global scope object}
 * using the inherited {@link #currentLanguageContext()} method from
 * {@link EasyScriptNode}.
 * Used by classes that access the global scope,
 * like global variable declaration or assignment,
 * so that they can have this Node as a child,
 * and then use the {@link com.oracle.truffle.api.library.CachedLibrary}
 * annotation in their {@link Specialization} methods.
 * Identical to the class with the same name from part 10.
 */
public abstract class GlobalScopeObjectExprNode extends EasyScriptExprNode {
    @Specialization
    protected DynamicObject returnGlobalScopeObject() {
        return this.currentLanguageContext().globalScopeObject;
    }
}
