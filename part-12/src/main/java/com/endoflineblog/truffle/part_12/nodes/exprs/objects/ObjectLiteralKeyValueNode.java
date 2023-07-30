package com.endoflineblog.truffle.part_12.nodes.exprs.objects;

import com.endoflineblog.truffle.part_12.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_12.runtime.JavaScriptObject;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class ObjectLiteralKeyValueNode extends EasyScriptNode {
    public abstract void executeObjectLiteralKeyValue(VirtualFrame frame, JavaScriptObject object);
}
