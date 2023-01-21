package com.endoflineblog.truffle.part_11.runtime;

import com.endoflineblog.truffle.part_11.EasyScriptTruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.staticobject.DefaultStaticProperty;
import com.oracle.truffle.api.staticobject.StaticProperty;
import com.oracle.truffle.api.staticobject.StaticShape;

/**
 * A Truffle static object that implements the built-in
 * {@code Math} JavaScript object.
 * Surfaces two properties, {@code abs} and {@code pow},
 * which are built-in functions that any EasyScript program can call.
 */
@ExportLibrary(InteropLibrary.class)
public final class MathObject implements TruffleObject {
    public static MathObject create(EasyScriptTruffleLanguage language,
            FunctionObject absFunction, FunctionObject powFunction) {
        StaticShape.Builder shapeBuilder = StaticShape.newBuilder(language);
        StaticProperty absProp = new DefaultStaticProperty("abs");
        StaticProperty powProp = new DefaultStaticProperty("pow");
        Object staticObject = shapeBuilder
                .property(absProp, Object.class, true)
                .property(powProp, Object.class, true)
                .build()
                .getFactory().create();
        absProp.setObject(staticObject, absFunction);
        powProp.setObject(staticObject, powFunction);
        return new MathObject(staticObject, absProp, powProp);
    }

    private final Object targetObject;
    private final StaticProperty absProp;
    private final StaticProperty powProp;

    private MathObject(Object targetObject, StaticProperty absProp, StaticProperty powProp) {
        this.targetObject = targetObject;
        this.absProp = absProp;
        this.powProp = powProp;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return "abs".equals(member) || "pow".equals(member);
    }

    @ExportMessage
    Object readMember(String member) throws UnknownIdentifierException {
        switch (member) {
            case "abs": return this.absProp.getObject(this.targetObject);
            case "pow": return this.powProp.getObject(this.targetObject);
            default: throw UnknownIdentifierException.create(member);
        }
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return new MemberNamesObject(new String[]{"abs", "pow"});
    }
}
