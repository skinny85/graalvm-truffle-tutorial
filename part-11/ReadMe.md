# Part 11 - strings, static method calls

In this part of the series,
we add support for strings to EasyScript,
our simplified JavaScript implementation.
To make them fully useful, we also add static
(meaning, without the support for object inheritance)
methods, demonstrated on the example of the
[`charAt()` string method](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/charAt).

## String literals

We support string literals starting with `'` and `"`
(we do not support literals starting with `` ` ``,
as we don't want to deal with [string interpolation](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Template_literals#syntax) -
since `${a}` can be transformed to `'' + a + ''` at parse time,
string interpolation doesn't require any support in the interpreter,
but it complicates parsing significantly)
by adding a new `string_literal` production to the `literal` non-terminal in the
[ANTLR grammar for EasyScript](src/main/antlr/com/endoflineblog/truffle/part_11/parsing/antlr/EasyScript.g4).

In the [parser code](src/main/java/com/endoflineblog/truffle/part_11/parsing/EasyScriptTruffleParser.java),
we make sure to handle escape sequences with backslashes,
so that character pairs like `\'` are turned to just `'`.
We do that with the [`StringEscapeUtils.unescapeJson()` method](https://commons.apache.org/proper/commons-text/apidocs/org/apache/commons/text/StringEscapeUtils.html#unescapeJson-java.lang.String-)
from the [Apache Commons Text library](https://commons.apache.org/proper/commons-text),
which we add as a [dependency to Gradle](build.gradle).

We will use the [`TruffleString` class](https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/TruffleStrings)
provided by Truffle to represent strings at runtime.
Given that, our [string literal Node](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/literals/StringLiteralExprNode.java)
simply creates an instance of that class from a Java string that we get from the parser.
We introduce a [helper class, `EasyScriptTruffleStrings`](src/main/java/com/endoflineblog/truffle/part_11/runtime/EasyScriptTruffleStrings.java),
that contains static utility methods that reduce duplication when working with `TruffleString`s
by centralizing things like the encoding used by the language.

## Expression changes

Because of the introduction of strings,
we need to modify a few of the existing expression nodes:

* The [`EasyScriptExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/EasyScriptExprNode.java)
  needs changes in the `executeBool()` method,
  as empty strings are considered "falsy" in JavaScript.
* The [`EqualityExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/comparisons/EqualityExprNode.java)
  and the [`InequalityExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/comparisons/InequalityExprNode.java)
  need a new specialization for `TruffleString`s,
  as strings can be compared with `===` and `!==` in JavaScript.
* The arithmetic comparison Nodes
  ([`GreaterExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/comparisons/GreaterExprNode.java),
  [`GreaterOrEqualExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/comparisons/GreaterOrEqualExprNode.java),
  [`LesserExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/comparisons/LesserExprNode.java)
  and [`LesserOrEqualExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/comparisons/LesserOrEqualExprNode.java))
  all need an extra specialization to handle `TruffleString`s,
  which be compared with operators like `>` in JavaScript.
* The [`AdditionExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/arithmetic/AdditionExprNode.java)
  can now represent string concatenation if either of the arguments is complex
  (complex values in JavaScript are anything other than numbers, booleans, `undefined`, and `null`).
  We add a "fast" specialization when both arguments are `TruffleString`s,
  and a generic specialization for when at least one of the arguments is a complex value. 
  In that specialization, we first coerce its arguments to Java strings using the
  `concatToStrings()` helper in `EasyScriptTruffleStrings`.
  That helper simply uses the `toString()` method of the arguments passed to it,
  and because of that, we need to tell Graal partial evaluation to not introspect that method
  (as those `toString()`s are fully virtual calls that cannot be statically resolved),
  which we do with the [`TruffleBoundary` annotation](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/CompilerDirectives.TruffleBoundary.html).

## Built-in methods

We add support for methods by introducing a new field in the
[`FunctionObject` class](src/main/java/com/endoflineblog/truffle/part_11/runtime/FunctionObject.java),
`methodTarget`. It will be `null` for function calls,
but non-`null` for method calls.
With that field in place, we have to update the
[`FunctionDispatchNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/functions/FunctionDispatchNode.java)
to pass the `methodTarget` (if it's non-`null`)
as the first argument when invoking the `call()`
method of either `DirectCallNode` or `IndirectCallNode`,
which we do by modifying the logic inside the existing `extendArguments()` method.

The actual implementation of the `charAt()` method is in the
[`CharAtMethodBodyExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/functions/built_in/methods/CharAtMethodBodyExprNode.java).
It's very similar to the Nodes for the built-in functions,
the only difference is that it expects an extra argument,
which is the string the method was called on.
We use the [`Shared` annotation](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/dsl/Cached.Shared.html)
as a small optimization to be able to share the `TruffleString`
operation Nodes between the two specializations -
which is possible, because these Nodes are stateless.

The `CallTarget` for this built-in method is created in the
[`TruffleLanguage` implementation for this chapter](src/main/java/com/endoflineblog/truffle/part_11/EasyScriptTruffleLanguage.java),
similarly to the `CallTarget` for the built-in functions,
and stored in a new [class, `StringPrototype`](src/main/java/com/endoflineblog/truffle/part_11/runtime/StringPrototype.java),
which is made available to Nodes by saving it in the
[`TruffleLanguage` Context](src/main/java/com/endoflineblog/truffle/part_11/EasyScriptLanguageContext.java)
as a new, `public`, field.

## Reading string properties

Since we're using `TruffleString`s,
we don't want to wrap them in a `TruffleObject`,
like we did for arrays with `ArrayObject` in the
[previous part of the series](../part-10),
as that would negate the performance benefits of using `TruffleString`s.
Instead, we have a dedicated Node
[class, `ReadTruffleStringPropertyNode`](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/strings/ReadTruffleStringPropertyNode.java),
that implements the logic of reading properties of a `TruffleString`.
The Node contains specializations for indexing into the string,
and also reading properties that are strings.
For the `charAt` property, it creates a `FunctionObject`
pointing at the `CallTarget` stored in the `StringPrototype`
available through `EasyScriptLanguageContext`.
In order to improve performance, it tries to cache the created `FunctionObject`,
but that caching is only valid if the target of the property read stays the same.
If we encounter more than 3 different targets for a given read of `charAt`,
we abandon caching, and instead switch to always creating a new `FunctionObject`.

With `ReadTruffleStringPropertyNode` now in place,
we can use it in the existing property access Nodes.
Since introducing strings to our language now makes it possible to access an object's property in two different ways
(with "direct" access, in code like `a.propName`,
and with "indexed" access, in code like `a['propName']`),
we create a new class,
[`ObjectPropertyReadNode`](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/properties/ObjectPropertyReadNode.java),
that contains the common logic of reading a property of an object.
Its first specialization covers the situation where the target of the read is a `TruffleString`,
in which case we simply delegate to `ReadTruffleStringPropertyNode`,
obtained through the `@Cached` annotation,
as it's a stateless Node;
the remaining 3 specializations were moved from the `PropertyReadExprNode` class,
[as it was in the previous part of the series](../part-10/src/main/java/com/endoflineblog/truffle/part_10/nodes/exprs/properties/PropertyReadExprNode.java).

Because of this refactoring,
we can change the
[`PropertyReadExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/properties/PropertyReadExprNode.java)
to simply delegate to `ObjectPropertyReadNode`.

For indexed property access,
we also use `ObjectPropertyReadNode`,
this time from the [`ArrayIndexReadExprNode` class](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/arrays/ArrayIndexReadExprNode.java),
but with an important addition:
we introduce specializations that handle the case when the index expression evaluates to a `TruffleString`
(in code like `"a"['length']`) - when that happens,
we need to convert `'length'` from a `TruffleString` to a Java string,
which is what `ObjectPropertyReadNode` expects.
We use the [`TruffleString.ToJavaStringNode` class](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/strings/TruffleString.ToJavaStringNode.html)
for that purpose.
We make sure to cache the Java `String` we create from the `TruffleString`,
but if a given indexed property access sees more than two different keys,
we switch to an uncached specialization instead.

## Benchmark

We have a [simple benchmark](src/jmh/java/com/endoflineblog/truffle/part_11/StringLengthBenchmark.java)
that performs a million string operations in a loop -
in one variant, using direct access, like `"abc".length`,
and in the other, an indexed access, like `"abc"['length']`.
We run it also for the GraalVM JavaScript implementation, for comparison.

Here are the results I get on my laptop:

```
Benchmark                                                  Mode  Cnt       Score      Error  Units
StringLengthBenchmark.count_while_char_at_direct_prop_ezs  avgt    5     577.467 ±   11.463  us/op
StringLengthBenchmark.count_while_char_at_direct_prop_js   avgt    5     582.202 ±   22.043  us/op
StringLengthBenchmark.count_while_char_at_index_prop_ezs   avgt    5     575.608 ±   13.571  us/op
StringLengthBenchmark.count_while_char_at_index_prop_js    avgt    5  126432.537 ± 5631.640  us/op
```

As we can see, there is no difference in performance between indexed and direct property access in EasyScript,
mainly because of the caching we implemented in
[`ArrayIndexReadExprNode`](src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/arrays/ArrayIndexReadExprNode.java).
However, indexed property access in the GraalVM JavaScript implementation is over 200
times slower than direct property access.
I've [opened an issue about this to the project](https://github.com/oracle/graaljs/issues/719),
and apparently it's a bug, fixed in GraalVM release `23.1.0`.

---

In addition to the benchmark, there are some
[unit tests](src/test/java/com/endoflineblog/truffle/part_11/StringsTest.java)
validating that the strings functionality works as expected.
