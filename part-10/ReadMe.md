# Part 10 - arrays, read-only properties

In this part of the series,
we add support for arrays to the language.
To use arrays effectively,
we also add the ability to read their `length` property.

## Array expressions

We support array literals, reading an array index,
and writing to an array index.
These new expressions are added to the
[ANTLR grammar for EasyScript](src/main/antlr/com/endoflineblog/truffle/part_10/parsing/antlr/EasyScript.g4).

The Nodes implementing those expressions are
[`ArrayLiteralExprNode`](src/main/java/com/endoflineblog/truffle/part_10/nodes/exprs/arrays/ArrayLiteralExprNode.java),
[`ArrayIndexReadExprNode`](src/main/java/com/endoflineblog/truffle/part_10/nodes/exprs/arrays/ArrayIndexReadExprNode.java)
and [`ArrayIndexWriteExprNode`](src/main/java/com/endoflineblog/truffle/part_10/nodes/exprs/arrays/ArrayIndexWriteExprNode.java),
respectively.
Accessing of the array elements is performed through a
[Truffle library](https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/TruffleLibraries),
[`InteropLibrary`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html),
which we've already seen in previous chapters.
However, we now use it directly, getting an instance of it with the
[`@CachedLibrary` annotation](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/library/CachedLibrary.html).
Using `@CachedLibrary` forces you to provide the `limit`
attribute of the `@Specialization` annotation.

The implementations of those Nodes use the
[`ArrayObject` class](src/main/java/com/endoflineblog/truffle/part_10/runtime/ArrayObject.java).
This is a Truffle [Dynamic Object](https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/DynamicObjectModel),
which means it implicitly implements the [`TruffleObject` interface](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/TruffleObject.html).
It exports the appropriate messages for dealing with arrays, like
[`getArraySize()`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html#getArraySize-java.lang.Object-),
[`readArrayElement()`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html#readArrayElement-java.lang.Object-long-)
and [`writeArrayElement()`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html#writeArrayElement-java.lang.Object-long-java.lang.Object-).

The [Shape for arrays](https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/DynamicObjectModel/#extended-object-layout),
cached in a field of the
[`TruffleLanguage` class](src/main/java/com/endoflineblog/truffle/part_10/EasyScriptTruffleLanguage.java)
for this chapter and then passed to the
[parser class](src/main/java/com/endoflineblog/truffle/part_10/parsing/EasyScriptTruffleParser.java),
is created by passing the `ArrayObject.class` to the
[`layout()` method](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/object/Shape.Builder.html#layout-java.lang.Class-)
of `Shape.Builder`.
There is a field in `ArrayObject` annotated with the
[`@DynamicField` annotation](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/object/DynamicObject.DynamicField.html)
that tells the Truffle object system that this dynamic object always has the `length` property.

The logic inside `ArrayObject` stores the `length` property directly in the object instance using a different Truffle library,
[`DynamicObjectLibrary`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/object/DynamicObjectLibrary.html).
To get a reference to an instance of the library,
we again use the `@CachedLibrary` annotation,
which can be placed not only on parameters of `@Specialization` methods,
but also of `@ExportMessage` methods.

We also extract a `TruffleObject` class
[called `MemberNamesObject`](src/main/java/com/endoflineblog/truffle/part_10/runtime/MemberNamesObject.java),
that simply encapsulates an array of property names,
and we use it to implement the
[`getMembers()` message](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html#getMembers-java.lang.Object-boolean-)
of `ArrayObject`.
We will use this class for the same purpose in other `TruffleObject`s below.

There are some
[unit tests](src/test/java/com/endoflineblog/truffle/part_10/ArraysTest.java)
validating that the array functionality works as expected.

## Reading properties

In order to allow reading the `length` property of arrays,
we need to change how we handle property reads.
In previous parts of the series, we implemented references like `Math.abs`
as two identifiers, separated by a `.`.
But now, we need to allow arbitrary expressions to be targets of property reads.

So, we add a new rule, `PropertyReadExpr5`, to the
[ANTLR grammar](src/main/antlr/com/endoflineblog/truffle/part_10/parsing/antlr/EasyScript.g4).
We implement that expression in the
[`PropertyReadExprNode` Node](src/main/java/com/endoflineblog/truffle/part_10/nodes/exprs/properties/PropertyReadExprNode.java).
We again use the `InteropLibrary`,
this time the
[`readMember()` method](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html#readMember-java.lang.Object-java.lang.String-)
message.

## `Math` static object

To still support references like `Math.abs` in our language,
we need to change the `Math` object.
Since we know exactly what properties `Math` has,
and we don't support property assignments in this chapter yet,
we will implement it using the opposite of Dynamic Object,
[Static Object](https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/StaticObjectModel).

The code is in the
[`MathObject` class](src/main/java/com/endoflineblog/truffle/part_10/runtime/MathObject.java).
The part dealing with static objects is in the
`create()` static factory method --
there, we initialize
[a `StaticShape`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/staticobject/StaticShape.html)
with two
[`StaticProperty` instances](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/staticobject/StaticProperty.html),
corresponding to the `abs` and `pow` properties of `Math`.
Finally, we create a new object from a factory retrieved from the Shape,
and save it, and the properties,
as instance fields of the `Math` object.
Then, the actual logic of reading the properties is implemented with messages from the `InteropLibrary`,
like [`readMember()`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html#readMember-java.lang.Object-java.lang.String-).

## Global scope as a Dynamic Object

Now that we know how to use the `DynamicObject` library,
we can fix a problem we had up to this point in the series with our interpreter.
The `GlobalScopeObject` uses a `HashMap`
to store the names and values of the global variables.
However, a `HashMap` is not optimized for partial evaluation,
which causes performance problems when JIT compiling it.
For that reason, the general rule in Truffle is to avoid using Java collections like `Map` or `List`,
and prefer using arrays instead,
which are much easier for Graal to optimize.
However, arrays are only appropriate for list-like sequences,
and for key-value pairs like `Map`,
we can use dynamic objects.

So, we change the
[`GlobalScopeObject` class](src/main/java/com/endoflineblog/truffle/part_10/runtime/GlobalScopeObject.java)
to be a `DynamicObject`, similar to what we did in `ArrayObject`.
We implement reading and writing the variables using `InteropLibrary` messages,
similarly like we did in `ArrayObject`.

This change to `GlobalScopeObject` means we need to adjust the Nodes that interact with global variables:
[`GlobalVarDeclStmtNode`](src/main/java/com/endoflineblog/truffle/part_10/nodes/stmts/variables/GlobalVarDeclStmtNode.java),
[`FuncDeclStmtNode`](src/main/java/com/endoflineblog/truffle/part_10/nodes/stmts/variables/FuncDeclStmtNode.java),
[`GlobalVarReferenceExprNode`](src/main/java/com/endoflineblog/truffle/part_10/nodes/exprs/variables/GlobalVarReferenceExprNode.java)
and [`GlobalVarAssignmentExprNode`](src/main/java/com/endoflineblog/truffle/part_10/nodes/exprs/variables/GlobalVarAssignmentExprNode.java).

We use the
[`flags` parameter](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/object/DynamicObjectLibrary.html#putConstant-com.oracle.truffle.api.object.DynamicObject-java.lang.Object-java.lang.Object-int-)
to save whether a given variable is a constant or not,
and we check that when performing assignment.

In order to be able to use the `@CachedLibrary` annotation with the `GlobalScopeObject`,
we create a special Node,
[`GlobalScopeObjectExprNode`](src/main/java/com/endoflineblog/truffle/part_10/nodes/exprs/GlobalScopeObjectExprNode.java),
that just returns the global scope object from the `TruffleLanguage` Context
using the `currentLanguageContext()` method defined in the
[base class of all Nodes](src/main/java/com/endoflineblog/truffle/part_10/nodes/EasyScriptNode.java).
We then add `GlobalScopeObjectExprNode` as the first child to of each Node that deals with global variables.
This way, they can receive the `GlobalScopeObject` instance as the first argument to their `@Specialization` methods,
and use it in the `@CachedLibrary` annotation.

### Performance results

Thanks to these improvements,
we can roll back the changes made in the
[last part of the series](../part-09) that turned `FunctionObject` mutable,
and remove caching the resolved function we added to `GlobalVarReferenceExprNode`
(which prevents code like `function f() {}; f = 3` from working correctly).

As it turns out, this simplified code is twice as fast on the Fibonacci benchmark than the complicated one that implemented
`GlobalScopeObject` with a `HashMap` in the previous part of the series:

```
Benchmark                              Mode  Cnt   Score   Error  Units
FibonacciBenchmark.recursive_ezs_eval  avgt    5  49.806 ± 0.835  us/op
FibonacciBenchmark.recursive_java      avgt    5  35.726 ± 0.497  us/op
FibonacciBenchmark.recursive_js_eval   avgt    5  72.937 ± 2.110  us/op
FibonacciBenchmark.recursive_sl_eval   avgt    5  52.396 ± 0.964  us/op
```
