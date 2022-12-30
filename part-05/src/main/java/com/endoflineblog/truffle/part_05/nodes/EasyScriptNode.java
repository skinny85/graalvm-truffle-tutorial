package com.endoflineblog.truffle.part_05.nodes;

import com.endoflineblog.truffle.part_05.EasyScriptLanguageContext;
import com.oracle.truffle.api.nodes.Node;

/**
 * The abstract common ancestor of all EasyScript AST Truffle Nodes.
 *
 * @see #currentLanguageContext()
 */
public abstract class EasyScriptNode extends Node {
    /** Allows retrieving the current Truffle language Context from within a Node. */
    protected final EasyScriptLanguageContext currentLanguageContext() {
        return EasyScriptLanguageContext.get(this);
    }
}
