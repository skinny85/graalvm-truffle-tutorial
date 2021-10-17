package com.endoflineblog.truffle.part_06.nodes.exprs.functions;

import com.endoflineblog.truffle.part_06.nodes.exprs.EasyScriptExprNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChild("argument")
public abstract class AbsFunctionBodyExprNode extends EasyScriptExprNode {
    @Specialization
    protected int intAbs(int argument) {
        return argument >= 0 ? argument : -argument;
    }

    @Specialization
    protected double intDouble(double argument) {
        return argument >= 0 ? argument : -argument;
    }
}
