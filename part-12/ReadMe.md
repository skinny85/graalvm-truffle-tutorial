# Part 12 - classes 1: methods, `new` operator

In this part of the series,
we add support for user-defined classes to EasyScript,
our simplified JavaScript implementation.
Since classes are quite complex to implement,
we will cover them across multiple parts.
In this first one, we handle class declarations with (instance) methods,
and the `new` operator.

## Parsing

In order to support classes,
we need to introduce the following changes to the
[ANTLR grammar](src/main/antlr/com/endoflineblog/truffle/part_12/parsing/antlr/EasyScript.g4)
for EasyScript:

* We need to introduce a new statement type to the grammar for class declarations.
  In this part, we only support
  [instance methods](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/Method_definitions)
  inside class declarations;
  since those are very similar to function declarations,
  just without the `function` keyword,
  we can reuse a lot of the grammar we have for functions already for methods
  by introducing a new production, `subroutine_decl`,
  that we use in both places.
* For parsing `new` expressions,
  we need to introduce a new precedence level that separates call and `new` operators,
  in order to parse code like `new A().a()` as a call, so `(new A()).a()`,
  instead of as a `new` expression (`new (A().a)()`).

In addition to the grammar changes, we also need to modify the
[parsing code](src/main/java/com/endoflineblog/truffle/part_12/parsing/EasyScriptTruffleParser.java)
to handle class declarations.
We create an instance of a new `TruffleObject`, the
[`ClassPrototypeObject` class](src/main/java/com/endoflineblog/truffle/part_12/runtime/ClassPrototypeObject.java),
and save it as a global variable, using the existing
[`GlobalVarDeclStmtNode` class](src/main/java/com/endoflineblog/truffle/part_12/nodes/stmts/variables/GlobalVarDeclStmtNode.java).
We will use that `ClassPrototypeObject` object in the implementation of the `new` operator.

Then, for methods, we re-use the code for parsing functions,
with the only difference being that we pass an instance of the new
[`DynamicObjectReferenceExprNode` class](src/main/java/com/endoflineblog/truffle/part_12/nodes/exprs/DynamicObjectReferenceExprNode.java)
to `FuncDeclStmtNode`
[that we changed](src/main/java/com/endoflineblog/truffle/part_12/nodes/stmts/variables/FuncDeclStmtNode.java)
to accept an `EasyScriptExprNode` instead of only
`GlobalScopeObjectExprNode` (other than that change of the type and the parameter name,
`FuncDeclStmtNode` is identical compared to the
[previous chapter](../part-11/src/main/java/com/endoflineblog/truffle/part_11/nodes/stmts/variables/FuncDeclStmtNode.java)).

## Nodes

For handling class declarations, we introduce a new
[class, `ClassDeclExprNode`](src/main/java/com/endoflineblog/truffle/part_12/nodes/exprs/objects/ClassDeclExprNode.java),
that simply executes all of its `FuncDeclStmtNode`
children Nodes that represent the class' method declarations,
and returns the `ClassPrototypeObject`
instance created during parsing that corresponds to that class' prototype.
That prototype instance is then saved as a global variable,
with the name equal to the class' name,
using the existing
[`GlobalVarDeclStmtNode` class](src/main/java/com/endoflineblog/truffle/part_12/nodes/stmts/variables/GlobalVarDeclStmtNode.java).

For creating class instances,
we introduce a new
[class, `NewExprNode`](src/main/java/com/endoflineblog/truffle/part_12/nodes/exprs/objects/NewExprNode.java),
that handles the `new` operator,
by creating a new instance of the
[`ClassInstanceObject` class](src/main/java/com/endoflineblog/truffle/part_12/runtime/ClassInstanceObject.java),
and passing it the `ClassPrototypeObject` resolved from its constructor child expression Node
(if the constructor child expression Node evaluates to something other than `ClassPrototypeObject`,
that's an error).
We use the [`@Executed` annotation](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/dsl/Executed.html)
to evaluate this constructor child expression,
while converting the `List` of arguments to `new` into an array in the constructor of the Node class,
but not executing it
(since, like we've seen since
[part 6](../part-06/ReadMe.md),
the Truffle DSL has a limitation where you can't use it for evaluating a variable amount of children Nodes).

## Benchmark

We introduce a
[simple benchmark](src/jmh/java/com/endoflineblog/truffle/part_12/InstanceMethodBenchmark.java)
that calls an instance method of a user-defined class in a loop,
to make sure our implementation is efficient.

We have two variants of the benchmark --
in one, we create the class instance inside the loop,
and in the other, we create the instance outside the loop.
We also run the benchmark for the GraalVM JavaScript implementation, for reference.

Here are the results I get when running the benchmark on my laptop:

```
Benchmark                                                        Mode  Cnt    Score    Error  Units
InstanceMethodBenchmark.count_method_prop_alloc_inside_for_ezs   avgt    5  295.620 ± 10.630  us/op
InstanceMethodBenchmark.count_method_prop_alloc_inside_for_js    avgt    5  293.406 ±  3.974  us/op
InstanceMethodBenchmark.count_method_prop_alloc_outside_for_ezs  avgt    5  294.061 ±  4.078  us/op
InstanceMethodBenchmark.count_method_prop_alloc_outside_for_js   avgt    5  296.810 ±  2.346  us/op
```

As we can see, the results are identical in all cases,
which means Graal was clever enough to eliminate the object allocation completely
(most likely by inlining the method body at the call site).

---

In addition to the benchmark, there are some
[unit tests](src/test/java/com/endoflineblog/truffle/part_12/ClassesTest.java)
that validate the class functionality works as expected.
