package com.endoflineblog.truffle.part_16.nodes.exprs.variables;

import com.endoflineblog.truffle.part_16.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_16.nodes.exprs.frame.AbstractFrameGetNode;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;

/**
 * A Node that represents the reference to a variable local to a function.
 * Very similar to the class with the same name from part 16,
 * the only difference is the {@link AbstractFrameGetNode}
 * that gets used for local variables in closures.
 */
@NodeChild(value = "currentOrParentFrameGetNode", type = AbstractFrameGetNode.class)
@NodeField(name = "frameSlot", type = int.class)
public abstract class LocalVarReferenceExprNode extends EasyScriptExprNode {
    protected abstract int getFrameSlot();

    @Specialization(guards = "currentOrParentFrame.isInt(getFrameSlot())")
    protected int readInt(Frame currentOrParentFrame) {
        return currentOrParentFrame.getInt(this.getFrameSlot());
    }

    @Specialization(guards = "currentOrParentFrame.isDouble(getFrameSlot())", replaces = "readInt")
    protected double readDouble(Frame currentOrParentFrame) {
        return currentOrParentFrame.getDouble(this.getFrameSlot());
    }

    @Specialization(guards = "currentOrParentFrame.isBoolean(getFrameSlot())")
    protected boolean readBool(Frame currentOrParentFrame) {
        return currentOrParentFrame.getBoolean(this.getFrameSlot());
    }

    @Specialization(replaces = {"readInt", "readDouble", "readBool"})
    protected Object readObject(Frame currentOrParentFrame) {
        return currentOrParentFrame.getObject(this.getFrameSlot());
    }
}
