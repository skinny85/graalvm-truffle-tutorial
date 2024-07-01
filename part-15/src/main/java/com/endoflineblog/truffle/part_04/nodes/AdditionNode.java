package com.endoflineblog.truffle.part_04.nodes;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

/**
 * The AST node that represents the plus operator in EasyScript.
 * Uses the Truffle DSL.
 * These few lines are all that's required to get equivalent functionality
 * to the hand-written code in {@link com.endoflineblog.truffle.part_02.AdditionNode part 2}.
 * The Truffle DSL annotation processor will generate a class extending this,
 * {@link AdditionNodeGen},
 * that implements the `execute*()` methods.
 */
// we want our generated node to have 2 children
@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class AdditionNode extends EasyScriptNode {
    /**
     * The integer addition specialization.
     * We invalidate it when it overflows,
     * hence the `rewriteOn` attribute
     * (Math.addExact() throws ArithmeticException when addition overflows).
     */
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int addInts(int leftValue, int rightValue) {
        return Math.addExact(leftValue, rightValue);
    }

    /**
     * The `double` addition specialization.
     * It is a superset of the `int` specialization
     * (we don't want the two active at the same time),
     * hence the `replaces` attribute.
     */
    @Specialization(replaces = "addInts")
    protected double addDoubles(double leftValue, double rightValue) {
        return leftValue + rightValue;
    }
}
