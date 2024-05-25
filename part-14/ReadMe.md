# Part 14 - classes 3: inheritance, `super`

In this part of the series,
we implement class inheritance,
and the `super` expression.
As part of these changes,
we also introduce `Object`,
which is a common superclass of all objects in JavaScript,
and implement one of its
[several instance methods in JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object#instance_methods),
[`hasOwnProperty()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/hasOwnProperty).

## Grammar

In order to support these features,
we need to introduce two changes to the
[ANTLR grammar](src/main/antlr/com/endoflineblog/truffle/part_14/parsing/antlr/EasyScript.g4)
for EasyScript:

1. We change the class declaration statement to add the optional `extends` clause.
2. We add a new `expr6` production that represents the `super` keyword.

## Parsing

[Our parser](src/main/java/com/endoflineblog/truffle/part_14/parsing/EasyScriptTruffleParser.java)
needs a few changes.
It now takes an instance of the [`ObjectPrototype` class](src/main/java/com/endoflineblog/truffle/part_14/runtime/ObjectPrototype.java)
as an argument, which represents the prototype of the built-in `Object` class
(the only class without a parent class in the language).
We save it in the `Stack` of `Map`s that we use for tracking function arguments and local variables under the key `"Object"`,
since user-defined classes can now extend it explicitly
(and you can also create instances of it, in code like `new Object()`).

When parsing a class declaration statement,
we handle the `extends` clause by searching in the first `Map` in the `Stack` for the prototype with that name,
and then save the prototype of the currently parsed class in that same `Map`,
so subsequent class declarations can reference it.

We also save the prototype of the class being currently parsed in a field of the parser,
so that we can pass it when encountering a `super` keyword
(`super`, unlike `this`, which is dynamic, is static in virtually all object-oriented languages,
which means it always refers to a specific class, regardless of the runtime type of a given instance).

## Unifying objects and prototypes

Since with inheritance, prototypes have themselves parent prototypes,
we unify the `JavaScriptObject` and `ClassPrototypeObject` classes
by [making `ClassPrototypeObject`](src/main/java/com/endoflineblog/truffle/part_14/runtime/ClassPrototypeObject.java)
[extend `JavaScriptObject`](src/main/java/com/endoflineblog/truffle/part_14/runtime/JavaScriptObject.java)
to avoid duplicating code between the two.

We change `JavaScriptObject` to use the
[interop library](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/interop/InteropLibrary.html)
instead of the [dynamic object library](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/object/DynamicObjectLibrary.html)
when reading properties of its prototype,
which allows classes to inherit methods from their superclass.

Because of that change, we have to modify the type of the prototype in `JavaScriptObject`
from `ClassPrototypeObject` to Truffle's `DynamicObject`,
as keeping it as `ClassPrototypeObject`, which now extends `JavaScriptObject`,
would make `ClassPrototypeObject` impossible to instantiate,
as it would always require another instance of `ClassPrototypeObject`
to be provided in its constructor.

To start the chain of `ClassPrototypeObject`s,
we need to have a prototype without a parent prototype
(the aforementioned [`ObjectPrototype` class](src/main/java/com/endoflineblog/truffle/part_14/runtime/ObjectPrototype.java)),
which extends `ClassPrototypeObject` by providing an anonymous subclass of `DynamicObject` as the prototype
(we can't pass `null` there, as `JavaScriptObject` object uses `@CachedLibrary` with the prototype field,
and you cannot use `@CachedLibrary` with a `null` value),
and then overrides the implementations of the property read messages from the interop library inherited from `JavaScriptObject`
to not reference the `prototype` field.

We create an instance of `ObjectPrototype` in the
[`TruffleLanguage` class for this part](src/main/java/com/endoflineblog/truffle/part_14/EasyScriptTruffleLanguage.java),
save it as a field next to the Shapes,
and pass it to the parser in the `parse()` method.
We also save it inside the
[`ShapesAndPrototypes` class](src/main/java/com/endoflineblog/truffle/part_14/common/ShapesAndPrototypes.java)
that we pass to the
[TruffleLanguage context class for this part](src/main/java/com/endoflineblog/truffle/part_14/EasyScriptLanguageContext.java).

## Constructor inheritance

Since constructors are regular properties in JavaScript,
they are also inherited from superclasses.
Because of that, we need to change the
[`NewExprNode` class](src/main/java/com/endoflineblog/truffle/part_14/nodes/exprs/objects/NewExprNode.java)
to use the interop library instead of dynamic object library,
since the constructor of a given class might be inherited from an ancestor class.

## `Object` methods

The implementation of the `Object.hasOwnProperty()` method is in the
[`HasOwnPropertyMethodBodyExprNode` class](src/main/java/com/endoflineblog/truffle/part_14/nodes/exprs/functions/built_in/methods/HasOwnPropertyMethodBodyExprNode.java),
and is very similar to the other built-in functions and methods, like `String.charAt()`.

In order to find this method when invoked on strings and primitives, we need to modify
[`ReadTruffleStringPropertyNode`](src/main/java/com/endoflineblog/truffle/part_14/nodes/exprs/strings/ReadTruffleStringPropertyNode.java)
and [`CommonReadPropertyNode`](src/main/java/com/endoflineblog/truffle/part_14/nodes/exprs/properties/CommonReadPropertyNode.java)
to read from the string or object prototype,
respectively, in their last specializations.

In `ReadTruffleStringPropertyNode`,
we need to convert any property we receive to a string,
we introduce a new method to the
[`EasyScriptTruffleStrings` class, `toStringOfMaybeString()`](src/main/java/com/endoflineblog/truffle/part_14/runtime/EasyScriptTruffleStrings.java),
that is deliberately not annotated with the
[`@TruffleBoundary` annotation](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/CompilerDirectives.TruffleBoundary.html)
that first checks whether the argument it's given is already a Java `String`,
in code like `"a".charAt()`, before delegating to `toString()`
from the [previous part](../part-13), which improves performance.

## `super()` in constructors

In order to allow calling parent constructors with `super()`,
we need to implement the `evaluateAsReceiver()` and `evaluateAsFunction()`
methods from the [previous part](../part-13/ReadMe.md#this)
in the [`SuperExprNode` class](src/main/java/com/endoflineblog/truffle/part_14/nodes/exprs/objects/SuperExprNode.java).
`evaluateAsReceiver()` is the same as `this`,
while `evaluateAsFunction()` needs to find the `"constructor"`
property in the prototype of the parent class.
Since we need to use the interop library to find that property,
as the constructor might have been defined on an ancestor class of the parent class,
we have to use the [`Node.insert()` method](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/nodes/Node.html#insert(com.oracle.truffle.api.nodes.Node))
to save an instance of it in a field, similarly to what `@CachedLibrary` does
(we can't use `@CachedLibrary` directly, since `evaluateAsFunction()`
is not a specialization method).

## `super` property reads

For reading properties of `super`,
since we need to change the algorithm of finding the property to start with the parent class prototype,
instead of `this` object.
We can implement that by treating `SuperExprNode` specially in
[`PropertyReadExprNode`](src/main/java/com/endoflineblog/truffle/part_14/nodes/exprs/properties/PropertyReadExprNode.java)
and [`ArrayIndexReadExprNode`](src/main/java/com/endoflineblog/truffle/part_14/nodes/exprs/arrays/ArrayIndexReadExprNode.java)
to read the parent prototype from the `SuperExprNode`
instance with the `readParentPrototype()` method.

**Note**: we don't have to do the same with the expression Nodes for writing properties,
[`PropertyWriteExprNode`](src/main/java/com/endoflineblog/truffle/part_14/nodes/exprs/properties/PropertyWriteExprNode.java)
and [`ArrayIndexWriteExprNode`](src/main/java/com/endoflineblog/truffle/part_14/nodes/exprs/arrays/ArrayIndexWriteExprNode.java),
since [writing to `super` writes to `this` in JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/super#setting_super.prop_sets_the_property_on_this_instead).

## Benchmark

We modify the benchmark from the
[last part](../part-13/src/jmh/java/com/endoflineblog/truffle/part_13/CounterThisBenchmark.java)
to add a
[class hierarchy](src/jmh/java/com/endoflineblog/truffle/part_14/CounterThisBenchmark.java)
to the `Counter` class.

Here are the results when running the benchmark on my laptop:

```
Benchmark                                                Mode  Cnt    Score    Error  Units
CounterThisBenchmark.count_with_this_in_for_direct_ezs   avgt    5  582.213 ± 19.996  us/op
CounterThisBenchmark.count_with_this_in_for_direct_js    avgt    5  705.399 ± 16.581  us/op
CounterThisBenchmark.count_with_this_in_for_indexed_ezs  avgt    5  575.528 ± 14.741  us/op
CounterThisBenchmark.count_with_this_in_for_indexed_js   avgt    5  707.888 ± 18.730  us/op
```

The EasyScript performance is identical to the
[last part](../part-13/ReadMe.md#benchmark),
while GraalVM JavaScript is slightly slower --
I assume because it's possible to change the prototype of an object in JavaScript with the
[`Object.setPrototype()` method](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/setPrototypeOf),
while the prototype of an object cannot be changed after instantiating it in EasyScript,
which might allow Graal to apply more aggressive optimizations in that case.

---

In addition to the benchmark, there are some
[unit tests](src/test/java/com/endoflineblog/truffle/part_14/InheritanceTest.java)
that validate the inheritance functionality works as expected.
