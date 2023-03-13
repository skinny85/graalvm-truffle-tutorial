package com.endoflineblog.truffle.part_11.nodes.exprs.strings;

import com.endoflineblog.truffle.part_11.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_11.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_11.runtime.FunctionObject;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;

@ImportStatic(EasyScriptTruffleStrings.class)
public abstract class ReadTruffleStringPropertyExprNode extends EasyScriptNode {
    protected static final TruffleString LENGTH_PROP = EasyScriptTruffleStrings.fromJavaString("length");
    protected static final TruffleString CHAR_AT_PROP = EasyScriptTruffleStrings.fromJavaString("charAt");
    protected static final TruffleString SUBSTRING_PROP = EasyScriptTruffleStrings.fromJavaString("substring");

    public abstract Object executeReadTruffleStringProperty(TruffleString truffleString, Object property);

    @Specialization
    protected TruffleString readArrayIndex(TruffleString truffleString,
            int index,
            @Cached TruffleString.SubstringNode substringNode) {
        return substringNode.execute(truffleString, index, 1, TruffleString.Encoding.UTF_16, true);
    }

    @Specialization(guards = "areEqual(propertyName, LENGTH_PROP, equalNode)")
    protected int readLengthProperty(TruffleString truffleString,
            @SuppressWarnings("unused") TruffleString propertyName,
            @Cached TruffleString.EqualNode equalNode,
            @Cached TruffleString.CodePointLengthNode lengthNode) {
        return lengthNode.execute(truffleString, TruffleString.Encoding.UTF_16);
    }

    @Specialization(guards = "areEqual(propertyName, CHAR_AT_PROP, equalNode)")
    protected FunctionObject readCharAtProperty(TruffleString truffleString,
            @SuppressWarnings("unused") TruffleString propertyName,
            @Cached TruffleString.EqualNode equalNode,
            @Cached("create(currentLanguageContext().stringPrototype.charAtMethod, 2, truffleString)") FunctionObject charAtMethod) {
        return charAtMethod;
    }

    @Specialization(guards = "areEqual(propertyName, SUBSTRING_PROP, equalNode)")
    protected FunctionObject readSubstringProperty(TruffleString truffleString,
            @SuppressWarnings("unused") TruffleString propertyName,
            @Cached TruffleString.EqualNode equalNode,
            @Cached("create(currentLanguageContext().stringPrototype.substringMethod, 3, truffleString)") FunctionObject substringMethod) {
        return substringMethod;
    }
}
