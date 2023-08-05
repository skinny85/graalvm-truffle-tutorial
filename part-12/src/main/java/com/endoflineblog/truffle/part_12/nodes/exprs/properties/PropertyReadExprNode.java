package com.endoflineblog.truffle.part_12.nodes.exprs.properties;

import com.endoflineblog.truffle.part_12.nodes.exprs.EasyScriptExprNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.objects.ObjectPropertyReadNode;
import com.endoflineblog.truffle.part_12.nodes.exprs.objects.ObjectPropertyReadNodeGen;
import com.endoflineblog.truffle.part_12.runtime.EasyScriptTruffleStrings;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;

/**
 * The Node for reading properties of objects.
 * Used in code like {@code t.myProp}.
 * Similar to the class with the same name from part 10,
 * the only difference is we add a specialization for when the target of the property read is a
 * {@link TruffleString}, in which case we delegate to {@link ReadTruffleStringPropertyExprNode}.
 *
 * @see #readPropertyOfString
 */
public final class PropertyReadExprNode extends EasyScriptExprNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private EasyScriptExprNode targetExpr;

    private final TruffleString propertyName;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private ObjectPropertyReadNode objectPropertyReadNode;

    public PropertyReadExprNode(EasyScriptExprNode targetExpr, String propertyName) {
        this.targetExpr = targetExpr;
        this.propertyName = EasyScriptTruffleStrings.fromJavaString(propertyName);
        this.objectPropertyReadNode = ObjectPropertyReadNodeGen.create();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object target = this.targetExpr.executeGeneric(frame);
        return this.objectPropertyReadNode.executePropertyRead(target, this.propertyName);
    }
}
