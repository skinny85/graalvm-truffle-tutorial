package com.endoflineblog.truffle.part_16.nodes.stmts.variables;

import com.endoflineblog.truffle.part_16.nodes.exprs.functions.FunctionDefinitionExprNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.Tag;

@NodeChild(value = "functionDefinitionExprNode", type = FunctionDefinitionExprNode.class)
@NodeField(name = "nestedFuncFrameSlot", type = int.class)
public abstract class NestedFuncDeclStmtNode extends EasyScriptStmtNode {
    protected abstract int getNestedFuncFrameSlot();

    protected NestedFuncDeclStmtNode() {
        super(null);
    }

    @Specialization
    protected Object declareNestedFunction(VirtualFrame frame, Object func) {
        frame.setObject(this.getNestedFuncFrameSlot(), func);

        return Undefined.INSTANCE;
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return false;
    }
}
