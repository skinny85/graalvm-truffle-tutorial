# Part 13 - classes 2: fields, `this`, constructors

In this part of the series,
we allow storing state inside class instances in EasyScript,
our simplified JavaScript implementation.
In order to support that,
we need to add the notion of property writes,
the `this` keyword, and constructors, to the language.

## Grammar

In order to support these features,
we need to introduce two changes to the
[ANTLR grammar](src/main/antlr/com/endoflineblog/truffle/part_13/parsing/antlr/EasyScript.g4)
for EasyScript:

1. We add a new `expr1` production (with the label `PropertyWriteExpr1`)
   that represents property writes.
2. We add a new `expr6` production that represents the `this` keyword.

## Property writes

Property writes are in many ways a mirror of property reads.
We introduce a
[new class, `PropertyWriteExprNode`](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/properties/PropertyWriteExprNode.java),
that represents the `PropertyWriteExpr1` grammar production from above,
and is the equivalent of `PropertyReadExprNode` that we've seen since
[part 10](../part-10/ReadMe.md#reading-properties).
It simply delegates to the 
[new `CommonWritePropertyNode` class](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/properties/CommonWritePropertyNode.java)
that is the equivalent of `CommonReadPropertyNode` that we've had since
[part 11](../part-11/ReadMe.md#reading-string-properties).
The existing
[`ArrayIndexWriteExprNode` class](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/arrays/ArrayIndexWriteExprNode.java)
also delegates to `CommonWritePropertyNode`,
but has to make sure that if the index evaluates to not a number,
it's converted to a Java string first
(if the index turns out to be a `TruffleString`,
in code like `myObj['myProp'] = value`,
it uses the same trick that `ArrayIndexReadExprNode` used in
[part 11](../part-11/ReadMe.md#reading-string-properties)
to cache the first two Java `String`s resulting from converting `TruffleString`s).

`CommonWritePropertyNode` itself is quite simple,
and uses the
[`writeMember` message from the `InteropLibrary`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html#writeMember-java.lang.Object-java.lang.String-java.lang.Object-)
to perform the actual property writes.

The handling of the `writeMember()` interop library message is in the new
[`JavaScriptObject` class](src/main/java/com/endoflineblog/truffle/part_13/runtime/JavaScriptObject.java)
(which is just the renamed
[`ClassInstanceObject` from the previous part](../part-12/src/main/java/com/endoflineblog/truffle/part_12/runtime/ClassInstanceObject.java)
to reflect its more generic nature).
Writing a property simply means saving it using the
[dynamic object library](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/object/DynamicObjectLibrary.html).

Note that performing writes also changes the logic of reads -
instead of always delegating to the prototype,
like in the previous part,
we now need to check first whether the object itself contains that property
(if it does, it shadows the one from the prototype).

## Built-in objects

Since property writes can be performed on any object,
not only on class instances,
we make the classes for the built-in objects,
[`FunctionObject`](src/main/java/com/endoflineblog/truffle/part_13/runtime/FunctionObject.java)
and [`ArrayObject`](src/main/java/com/endoflineblog/truffle/part_13/runtime/ArrayObject.java),
extend `JavaScriptObject`,
in order not to duplicate the logic of writing properties.

Speaking of `ArrayObject`, it needs to make sure it handles writing the `length`
property, which is special for arrays:
you can only write a non-negative integer value to it
(any attempt to write a negative integer, or a non-integer, will result in an error),
and if the value written is different from the current length of the array,
it gets resized to match the value written.
We handle that in `ArrayObject` by exporting the `writeMember` message as a static nested class,
instead of a method
(similarly like with methods,
either the name of the class must be equal to the (capitalized) name of the message from the library,
or you can use the `name` attribute of `@ExportMessage`, and then the class can have any name).
Inside that class, we can write `@Specialization` methods,
similarly like we can in Node classes
(with the one significant difference that the specializations inside the message class must be `static`,
and thus must take the object the message is being invoked on as the first argument).

Since now all objects extend `JavaScript` object,
we must provide not only a `Shape` when creating them,
but also an instance of
[`ClassPrototypeObject`](src/main/java/com/endoflineblog/truffle/part_13/runtime/ClassPrototypeObject.java),
which is unchanged from the [previous part](../part-12/ReadMe.md).
Since arrays and functions all have their own prototype,
we introduce
[a class, `ShapesAndPrototypes`](src/main/java/com/endoflineblog/truffle/part_13/common/ShapesAndPrototypes.java),
grouping all of them, alongside the required Shapes, in one place.
We make an instance of this new class available from the
[`TruffleLanguage` context](src/main/java/com/endoflineblog/truffle/part_13/EasyScriptLanguageContext.java)
instance.
Naturally, we create an instance of the `TruffleLanguage` context class in the
[`TruffleLanguage` implementation for this part](src/main/java/com/endoflineblog/truffle/part_13/EasyScriptTruffleLanguage.java).

Then, `ShapesAndPrototypes` can be used, through `EasyScriptLanguageContext`, in
[`ArrayLiteralExprNode`](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/arrays/ArrayLiteralExprNode.java)
and [`FuncDeclStmtNode`](src/main/java/com/endoflineblog/truffle/part_13/nodes/stmts/variables/FuncDeclStmtNode.java).

## `this`

The `this` keyword is implemented in the
[`ThisExprNode` class](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/objects/ThisExprNode.java)
by simply reading the first argument from the `VirtualFrame`.
Reserving the first argument for `this` has wide-ranging repercussions on how function and method calls work in our language.

First, we need to offset all arguments for user-defined functions and methods by one,
to leave the argument with index `0` reserved for `this`.
In EasyScript, we do it directly
[in the parser](src/main/java/com/endoflineblog/truffle/part_13/parsing/EasyScriptTruffleParser.java),
in the `parseSubroutineDecl()` method.

Second, we need to also offset the arguments for the built-in functions
(not for built-in methods though, since those already have an explicit argument for `this`),
and we do that in the
[`TruffleLanguage` class](src/main/java/com/endoflineblog/truffle/part_13/EasyScriptTruffleLanguage.java).

Finally, and most importantly, we need to adjust the code that performs function and method calls,
to pass the `this` argument.
We add a new argument to the
[`executeDispatch()` method of `FunctionDispatchNode`](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/functions/FunctionDispatchNode.java)
that represents the receiver of a method call,
and make sure it's the first element of the array of arguments passed to the `CallTarget`
representing a given function or method.

Where do we get that argument from?
We add two new methods to the root of our expression Node hierarchy,
[`EasyScriptExprNode`](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/EasyScriptExprNode.java),
`evaluateAsReceiver()` and `evaluateAsFunction()`.
The vast majority of Node classes in EasyScript use the default implementation of these methods
(which are simply to return `undefined`, and delegate to `executeGeneric()`, respectively),
the only exception are the property read Nodes.

These two new methods are only called from the
[`FunctionCallExprNode` class](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/functions/FunctionCallExprNode.java),
which uses the value returned from `evaluateAsReceiver()`
as the `receiver` argument it passes to `FunctionDispatchNode.executeDispatch()`.

## Property reads

The property read Nodes are the only Nodes that override the default implementations of
`evaluateAsReceiver()` and `evaluateAsFunction()`.
[`PropertyReadExprNode`](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/properties/PropertyReadExprNode.java)
implements `evaluateAsReceiver()` by executing only the target expression,
and `evaluateAsFunction()` by delegating to its one specialization method
(in [part 12](../part-12/src/main/java/com/endoflineblog/truffle/part_12/nodes/exprs/properties/PropertyReadExprNode.java),
that specialization used a `@Cached` argument of type `CommonReadPropertyNode`,
but since we won't have an instance of `CommonReadPropertyNode` available in `evaluateAsFunction()`,
we switch to using a field annotated with `@Child`,
and initializing the field with an explicitly created instance of `CommonReadPropertyNode`).

But while that is easy to implement for `PropertyReadExprNode`,
since it only has a single specialization,
[`ArrayIndexReadExprNode` in part 12](../part-12/src/main/java/com/endoflineblog/truffle/part_12/nodes/exprs/arrays/ArrayIndexReadExprNode.java)
has 4 specializations.
So, how can we implement `evaluateAsFunction()` in that case?
We use a trick: we create a
[static inner Node inside `ArrayIndexReadExprNode`](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/arrays/ArrayIndexReadExprNode.java),
and move all specializations to that class
(in fact, we have to add a fifth specialization in this part,
to convert a non-int, non-string index to a string before delegating to `CommonReadPropertyNode`,
same as we did in [`ArrayIndexWriteExprNode`](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/arrays/ArrayIndexWriteExprNode.java)).
With that change, `ArrayIndexReadExprNode` now only contains a single specialization that delegates to the inner Node,
and we can implement `evaluateAsFunction()` in a similar way as we did for `PropertyReadExprNode`,
by calling that one specialization.

Finally, since we changed the way we resolve targets of method calls,
we no longer need the complicated caching we used in
[`ReadTruffleStringPropertyNode` since part 11](../part-11/src/main/java/com/endoflineblog/truffle/part_11/nodes/exprs/strings/ReadTruffleStringPropertyNode.java).
So, we can [change `ReadTruffleStringPropertyNode`](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/strings/ReadTruffleStringPropertyNode.java)
to read properties directly from the string prototype
(we don't have to worry about instance properties shadowing the ones from the prototype,
since strings are immutable in JavaScript).

## Constructors

And for the last feature, we just need to make a small change to the
[`NewExprNode` class](src/main/java/com/endoflineblog/truffle/part_13/nodes/exprs/objects/NewExprNode.java)
to check whether the prototype of a given class has a method with the name "constructor",
and if it does, call it using `FunctionDispatchNode`.

## Benchmark

We introduce a
[simple benchmark](src/jmh/java/com/endoflineblog/truffle/part_13/CounterThisBenchmark.java)
that calls an instance method of a user-defined class in a loop,
to make sure our implementation is efficient.
We have two variants of the benchmark:
one that uses direct property accesses,
and another that uses indexed property accesses.

Here are the results when running the benchmark on my laptop:

```
Benchmark                                                Mode  Cnt    Score    Error  Units
CounterThisBenchmark.count_with_this_in_for_direct_ezs   avgt    5  577.478 ± 36.396  us/op
CounterThisBenchmark.count_with_this_in_for_direct_js    avgt    5  571.999 ± 21.203  us/op
CounterThisBenchmark.count_with_this_in_for_indexed_ezs  avgt    5  579.777 ± 31.468  us/op
CounterThisBenchmark.count_with_this_in_for_indexed_js   avgt    5  576.204 ± 25.755  us/op
```

Direct and indexed property access have the same performance,
both in EasyScript, and in the GraalVM JavaScript implementation.

---

In addition to the benchmark, there are some
[unit tests](src/test/java/com/endoflineblog/truffle/part_13/FieldsTest.java)
that validate the fields functionality works as expected.
