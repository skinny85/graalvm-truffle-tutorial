package com.endoflineblog.truffle.part_16.runtime;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

public final class Environment extends DynamicObject {
     public Environment(Shape shape) {
        super(shape);
    }
}
