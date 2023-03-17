package com.endoflineblog.truffle.part_11.nodes.exprs.strings;

import com.endoflineblog.truffle.part_11.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_11.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_11.runtime.FunctionObject;
import com.endoflineblog.truffle.part_11.runtime.Undefined;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ReadTruffleStringPropertyExprNode extends EasyScriptNode {
    protected static final String LENGTH_PROP = "length";
    protected static final TruffleString LENGTH_PROP_TS = EasyScriptTruffleStrings.fromJavaString(LENGTH_PROP);
    protected static final String SUBSTRING_PROP = "substring";
    protected static final TruffleString SUBSTRING_PROP_TS = EasyScriptTruffleStrings.fromJavaString(SUBSTRING_PROP);

    public abstract Object executeReadTruffleStringProperty(TruffleString truffleString, Object property);

    @Specialization
    protected TruffleString readArrayIndex(
            TruffleString truffleString,
            int index,
            @Cached TruffleString.SubstringNode substringNode) {
        return substringNode.execute(truffleString, index, 1, TruffleString.Encoding.UTF_16, true);
    }

    @Specialization(guards = "LENGTH_PROP.equals(propertyName)")
    protected int readLengthProperty(
            TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName,
            @Cached TruffleString.CodePointLengthNode lengthNode) {
        return lengthNode.execute(truffleString, TruffleString.Encoding.UTF_16);
    }

    @Specialization(guards = "equals(LENGTH_PROP_TS, propertyName, equalNode)")
    protected int readLengthProperty(
            TruffleString truffleString,
            @SuppressWarnings("unused") TruffleString propertyName,
            @Cached @SuppressWarnings("unused") TruffleString.EqualNode equalNode,
            @Cached TruffleString.CodePointLengthNode lengthNode) {
        return lengthNode.execute(truffleString, TruffleString.Encoding.UTF_16);
    }

    @Specialization(guards = {
            "SUBSTRING_PROP.equals(propertyName)",
            "same(substringMethod.methodTarget, truffleString)"
    })
    protected FunctionObject readSubstringProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") String propertyName,
            @Cached("create(currentLanguageContext().stringPrototype.substringMethod, 3, truffleString)") FunctionObject substringMethod) {
        return substringMethod;
    }

    @Specialization(guards = {
            "equals(SUBSTRING_PROP_TS, propertyName, equalNode)",
            "same(substringMethod.methodTarget, truffleString)"
    })
    protected FunctionObject readSubstringProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") TruffleString propertyName,
            @Cached @SuppressWarnings("unused") TruffleString.EqualNode equalNode,
            @Cached("create(currentLanguageContext().stringPrototype.substringMethod, 3, truffleString)") FunctionObject substringMethod) {
        return substringMethod;
    }

    /** Accessing any other string property should return 'undefined'. */
    @Specialization
    protected Undefined readUnknownProperty(
            @SuppressWarnings("unused") TruffleString truffleString,
            @SuppressWarnings("unused") Object property) {
        return Undefined.INSTANCE;
    }
}
