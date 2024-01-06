package com.endoflineblog.truffle.part_13.nodes.exprs.arrays;

import com.endoflineblog.truffle.part_13.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.properties.CommonReadPropertyNode;
import com.endoflineblog.truffle.part_13.nodes.exprs.properties.CommonReadPropertyNodeGen;
import com.endoflineblog.truffle.part_13.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The Node representing reading array indexes
 * (like {@code a[1]}).
 */
@NodeChild("arrayExpr")
@NodeChild("indexExpr")
@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ArrayIndexReadExprNode extends EasyScriptExprNode {
    @ImportStatic(EasyScriptTruffleStrings.class)
    static abstract class PropertyNameCacheNode extends Node {
        abstract Object executePropertyNameCache(Object property);

        @Specialization(guards = "equals(propertyName, cachedPropertyName, equalNode)", limit = "2")
        protected String truffleStringPropertyNameCached(
                @SuppressWarnings("unused") TruffleString propertyName,
                @Cached @SuppressWarnings("unused") TruffleString.EqualNode equalNode,
                @Cached("propertyName") @SuppressWarnings("unused") TruffleString cachedPropertyName,
                @Cached @SuppressWarnings("unused") TruffleString.ToJavaStringNode toJavaStringNode,
                @Cached("toJavaStringNode.execute(cachedPropertyName)") String javaStringPropertyName) {
            return javaStringPropertyName;
        }

        @Specialization(replaces = "truffleStringPropertyNameCached")
        protected String truffleStringPropertyNameUncached(
                TruffleString propertyName,
                @Cached TruffleString.ToJavaStringNode toJavaStringNode) {
            return toJavaStringNode.execute(propertyName);
        }

        @Fallback
        protected Object nonTruffleStringPropertyName(Object propertyName) {
            return propertyName;
        }
    }

    protected abstract EasyScriptExprNode getArrayExpr();
    protected abstract EasyScriptExprNode getIndexExpr();

    @Child
    private CommonReadPropertyNode commonReadPropertyNode;

    @Child
    private PropertyNameCacheNode propertyNameCacheNode;

    @Specialization
    protected Object readIndexOrProperty(Object target, Object indexOrProperty) {
        return this.getOrCreateCommonReadPropertyNode().executeReadProperty(target,
                this.getOrCreatePropertyNameCacheNode().executePropertyNameCache(indexOrProperty));
    }

    @Override
    public Object evaluateAsReceiver(VirtualFrame frame) {
        return this.getArrayExpr().executeGeneric(frame);
    }

    @Override
    public Object evaluateAsFunction(VirtualFrame frame, Object receiver) {
        Object property = this.getIndexExpr().executeGeneric(frame);
        return this.readIndexOrProperty(receiver, property);
    }

    private CommonReadPropertyNode getOrCreateCommonReadPropertyNode() {
        if (this.commonReadPropertyNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.commonReadPropertyNode = CommonReadPropertyNodeGen.create();
            this.insert(commonReadPropertyNode);
        }
        return this.commonReadPropertyNode;
    }

    private PropertyNameCacheNode getOrCreatePropertyNameCacheNode() {
        if (this.propertyNameCacheNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.propertyNameCacheNode = ArrayIndexReadExprNodeGen.PropertyNameCacheNodeGen.create();
            this.insert(propertyNameCacheNode);
        }
        return this.propertyNameCacheNode;
    }
}
