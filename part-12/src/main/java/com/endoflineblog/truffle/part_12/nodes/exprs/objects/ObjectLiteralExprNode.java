package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import java.util.List;

public final class ObjectLiteralExprNode extends EasyScriptExprNode {
    @Children
    private final ObjectLiteralKeyValueNode[] members;

    public ObjectLiteralExprNode(List<ObjectLiteralKeyValueNode> keyValueNodes) {
        this.members = keyValueNodes.toArray(new ObjectLiteralKeyValueNode[]{});
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        EasyScriptLanguageContext easyScriptLanguageContext = this.currentLanguageContext();
        JavaScriptObject object = new JavaScriptObject(easyScriptLanguageContext.objectShape);
        for (int i = 0; i < this.members.length; i++) {
            this.members[i].executeObjectLiteralKeyValue(frame, object);
        }
        return object;
    }
}
