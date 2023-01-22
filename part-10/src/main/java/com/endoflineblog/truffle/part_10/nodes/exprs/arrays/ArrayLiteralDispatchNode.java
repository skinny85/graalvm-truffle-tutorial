package com.endoflineblog.truffle.part_10.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_10.runtime.ArrayObject;
import com.endoflineblog.truffle.part_10.runtime.IntArrayObject;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.Shape;

public abstract class ArrayLiteralDispatchNode extends Node {
    public abstract Object executeDispatch(Shape arrayShape, Object[] arrayElements);

    @Specialization
    protected static Object intArray(Shape arrayShape, Integer[] ints) {
        return new IntArrayObject(arrayShape, ints);
    }

    @Specialization(replaces = "intArray")
    protected static Object objectArray(Shape arrayShape, Object[] arrayElements) {
        return new ArrayObject(arrayShape, arrayElements);
    }
}
