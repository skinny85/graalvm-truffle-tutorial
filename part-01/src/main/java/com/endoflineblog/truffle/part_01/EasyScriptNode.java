package com.endoflineblog.truffle.part_01;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

/**
 * This is the common superclass of all AST nodes in EasyScript.
 * It defines an execute method that all subclasses must implement.
 *
 * @see #executeInt
 * @see IntLiteralNode
 * @see AdditionNode
 */
public abstract class EasyScriptNode extends Node {
    /**
     * This is the 'evaluate' method that is used by the interpreter
     * to execute the abstract syntax tree.
     * Truffle expects you to define one yourself because of specializations,
     * which are different for each language
     * (we cover specializations in later parts).
     * Truffle places some restrictions on this method:
     * its name must start with the word 'execute',
     * and it has to take a {@link VirtualFrame} as an argument.
     *
     * @param frame the reference to a call stack activation record,
     *   used for things like local variables.
     *   Truffle requires your execute method take a {@link VirtualFrame}
     *   instance as an argument
     *
     * @return an integer that is the result of executing this AST subtree
     */
    public abstract int executeInt(VirtualFrame frame);
}
