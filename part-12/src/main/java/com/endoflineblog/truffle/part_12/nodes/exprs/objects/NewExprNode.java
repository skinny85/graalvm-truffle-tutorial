package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.exceptions.EasyScriptException;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.runtime.ClassInstanceObject;
import com.endoflineblog.truffle.part_12.runtime.ClassPrototypeObject;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

import java.util.List;

@NodeChild("constructorExpr")
public abstract class NewExprNode extends EasyScriptExprNode {
    protected NewExprNode(List<EasyScriptExprNode> args) {
        // ToDo consume the constructor arguments
    }

    @Specialization
    protected Object instantiateObject(ClassPrototypeObject classPrototypeObject) {
        return new ClassInstanceObject(classPrototypeObject);
    }

    @Fallback
    protected Object instantiateNonConstruct(Object object) {
        throw new EasyScriptException("'" + object + "' is not a constructor");
    }
}
