package com.endoflineblog.truffle.part_13.runtime;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * A {@link TruffleObject} that is the base class of all objects in EasyScript,
 * including user-defined class instances, and built-in objects like arrays and functions.
 * Very similar to {@code ClassInstanceObject} from part 12,
 * but with added support for writing properties,
 * through the {@link InteropLibrary} interface,
 * that is invoked in {@link com.endoflineblog.truffle.part_13.nodes.exprs.properties.CommonWritePropertyNode}.
 */
@ExportLibrary(InteropLibrary.class)
public class JavaScriptObject extends DynamicObject {
    final ClassPrototypeObject classPrototypeObject;

    public JavaScriptObject(Shape shape, ClassPrototypeObject classPrototypeObject) {
        super(shape);
        this.classPrototypeObject = classPrototypeObject;
    }

    @Override
    public String toString() {
        return "[object Object]";
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return this.toString();
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member,
            @Cached @Shared("instance") DynamicObject.ContainsKeyNode instanceContainsKeyNode,
            @Cached @Shared("prototype") DynamicObject.ContainsKeyNode prototypeContainsKeyNode) {
        return instanceContainsKeyNode.execute(this, member) ||
                prototypeContainsKeyNode.execute(this.classPrototypeObject, member);
    }

    @ExportMessage
    Object readMember(String member,
            @Cached DynamicObject.GetNode instanceGetNode,
            @Cached DynamicObject.GetNode prototypeGetNode)
            throws UnknownIdentifierException {
        Object value = instanceGetNode.execute(this, member, null);
        if (value == null) {
            value = prototypeGetNode.execute(this.classPrototypeObject, member, null);
        }
        if (value == null) {
            throw UnknownIdentifierException.create(member);
        }
        return value;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
            @Cached DynamicObject.GetKeyArrayNode getKeyArrayNode) {
        return new MemberNamesObject(getKeyArrayNode.execute(this));
    }

    @ExportMessage
    boolean isMemberModifiable(String member,
            @Cached @Shared("instance") DynamicObject.ContainsKeyNode instanceContainsKeyNode,
            @Cached @Shared("prototype") DynamicObject.ContainsKeyNode prototypeContainsKeyNode) {
        return this.isMemberReadable(member, instanceContainsKeyNode, prototypeContainsKeyNode);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
            @Cached @Shared("instance") DynamicObject.ContainsKeyNode instanceContainsKeyNode,
            @Cached @Shared("prototype") DynamicObject.ContainsKeyNode prototypeContainsKeyNode) {
        return !this.isMemberModifiable(member, instanceContainsKeyNode, prototypeContainsKeyNode);
    }

    @ExportMessage
    void writeMember(String member, Object value,
            @Cached DynamicObject.PutNode putNode) {
        putNode.execute(this, member, value);
    }
}
