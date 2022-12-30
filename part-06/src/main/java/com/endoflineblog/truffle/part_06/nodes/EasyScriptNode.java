package com.endoflineblog.truffle.part_06.nodes;

import com.endoflineblog.truffle.part_06.EasyScriptLanguageContext;
import com.oracle.truffle.api.nodes.Node;

/**
 * The abstract common ancestor of all EasyScript AST Truffle Nodes.
 * Identical to the class with the same name from part 5.
 */
public abstract class EasyScriptNode extends Node {
    /** Allows retrieving the current Truffle language Context from within a Node. */
    protected final EasyScriptLanguageContext currentLanguageContext() {
        return EasyScriptLanguageContext.get(this);
    }
}
