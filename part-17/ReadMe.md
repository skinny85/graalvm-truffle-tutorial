# Part 17 - anonymous functions and closures

In this part of the series,
we add support for nested and anonymous functions
(specifically, [arrow functions](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/Arrow_functions)),
to EasyScript, our simplified subset of JavaScript.
Both of these features require the language to implement a concept called
[closures](https://en.wikipedia.org/wiki/Closure_(computer_programming)),
which means that the inner function can access the arguments and local variables of its enclosing function,
even after the enclosing function has returned.

In EasyScript, we make a simplification:
we treat every anonymous function and every function declared inside another function as a closure.
In a real language, you would probably want to do some more sophisticated static analysis,
since not all anonymous and nested functions need to be closures --
only those that actually reference any non-global variable outside their lexical scope.

## Grammar and parsing

In order to support anonymous arrow functions,
we add a new expression type, `LambdaExpr1`, to the
[ANTLR grammar](src/main/antlr/com/endoflineblog/truffle/part_17/parsing/antlr/EasyScript.g4).

[Our parser](src/main/java/com/endoflineblog/truffle/part_17/parsing/EasyScriptTruffleParser.java)
needs a few changes.
We track the current function nesting level in a field,
and when introducing a new function argument or local variable,
we save the nesting level on which it was defined.
When encountering a reference to a variable,
we compare its nesting level to the current nesting level,
and create a number of `ParentFrameGetNode`s
equal to the difference between the two,
wrapped around a single `CurrentFrameGetNode`.
This way, we can support referencing function arguments and local variables at arbitrary levels of nesting.
We do the same for assignment expressions.

## `FunctionObject`

We add a new field of type `MaterializedFrame` to the
[`FunctionObject` class](src/main/java/com/endoflineblog/truffle/part_17/runtime/FunctionObject.java).
We pass `null` for non-closure functions,
and a non-`null` materialized frame for closures.
We use that field in the
[`FunctionDispatchNode` class](src/main/java/com/endoflineblog/truffle/part_17/nodes/exprs/functions/FunctionDispatchNode.java),
where we pass the `MaterializedFrame` as the second argument
(after `this`) to the underlying `CallTarget` if it's not `null`,
and offset the remaining arguments by an extra index in that case.

## Function declaration expressions and statements

In previous parts of the tutorial,
the two components of evaluating a function definition --
creating (and caching) a `FunctionObject` for it,
and then saving it in the global scope --
were both contained in a single class,
`FuncDeclStmtNode`, since the language only allowed global function definitions.
Now that we want to support nested functions and lambda expressions,
this assumption no longer holds, and we need to separate them.

The first piece is a new function definition expression,
the [`FuncDefExprNode` class](src/main/java/com/endoflineblog/truffle/part_17/nodes/exprs/functions/FuncDefExprNode.java).
It works in two modes: one for non-closures,
where we can cache the entire `FunctionObject`,
and one for closures, where we need to materialize the frame on every execution,
using the [`materialize()` method the `VirtualFrame` interface](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/frame/Frame.html#materialize(%29),
and so we can only cache the `CallTarget`,
and we have to re-create the `FunctionObject`
with the materialized frame each time.

We refactor [`FuncDeclStmtNode`](src/main/java/com/endoflineblog/truffle/part_17/nodes/stmts/variables/FuncDeclStmtNode.java)
to use `FuncDefExprNode` as a child Node,
and introduce a new statement,
[`NestedFuncDeclStmtNode`](src/main/java/com/endoflineblog/truffle/part_17/nodes/stmts/variables/NestedFuncDeclStmtNode.java),
for function definitions nested inside other functions.
In that case, we save the resulting `FunctionObject`
not in the global object, but as a local variable of the current frame
(we allocate the slot in the frame during parsing,
the same way we do for other local variables).

## Frame access

Once we have the `MaterializedFrame` available as the second argument of a closure,
we need to modify the way the closure reads the function arguments and local variables of its parent function,
to use that `MaterializedFrame` argument.
We want to leverage type specializations when reading and writing those parent local variables,
the same way we do for "regular" local variables since
[part 7](../part-07/ReadMe.md),
but we don't want to introduce duplication, and have two almost identical Node classes.

The solution is a variant of a technique we previously used with the
`GlobalScopeObjectExprNode` from
[part 10](../part-10/src/main/java/com/endoflineblog/truffle/part_10/nodes/exprs/GlobalScopeObjectExprNode.java).
We introduce a new Node hierarchy that returns the correct
[`Frame` instance](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/frame/Frame.html),
depending on how many levels of nesting we read or write the variables from.
Since we will have multiple different classes in this hierarchy,
we introduce a separate abstract superclass with its own `executeFrame()` method,
the [`AbstractFrameGetNode` class](src/main/java/com/endoflineblog/truffle/part_17/nodes/exprs/frame/AbstractFrameGetNode.java).

The first implementation,
[`CurrentFrameGetNode`](src/main/java/com/endoflineblog/truffle/part_17/nodes/exprs/frame/CurrentFrameGetNode.java),
is for accessing variables defined on the same level,
and simply returns the current frame.
The second implementation,
[`ParentFrameGetNode`](src/main/java/com/endoflineblog/truffle/part_17/nodes/exprs/frame/ParentFrameGetNode.java),
reads the `MaterializedFrame` from the second argument in the frame
(put there by `FunctionDispatchNode`).
However, it doesn't use the frame directly,
but instead gets it from another instance of `AbstractFrameGetNode`,
so that we can compose them,
and thus read variables from any level of nesting.

With this hierarchy in place,
we add it as a child Node to the
[`ReadFunctionArgExprNode` class](src/main/java/com/endoflineblog/truffle/part_17/nodes/exprs/functions/ReadFunctionArgExprNode.java),
and use it to read function arguments from the `Frame`
returned by executing the `AbstractFrameGetNode` child,
instead of from the `VirtualFrame` passed as an argument to `executeGeneric()`.
This way, the same `ReadFunctionArgExprNode` can be used for both
closures and non-closures
(the difference between the two being only a different `AbstractFrameGetNode` instance used).

We do something very similar for
[`WriteFunctionArgExprNode`](src/main/java/com/endoflineblog/truffle/part_17/nodes/exprs/functions/WriteFunctionArgExprNode.java),
[`LocalVarReferenceExprNode`](src/main/java/com/endoflineblog/truffle/part_17/nodes/exprs/variables/LocalVarReferenceExprNode.java),
and [`LocalVarAssignmentExprNode`](src/main/java/com/endoflineblog/truffle/part_17/nodes/exprs/variables/LocalVarAssignmentExprNode.java) --
in the last two, the `AbstractFrameGetNode` is used inside specialization methods.

## Benchmark

To measure how performant closures are,
we introduce a [benchmark](src/jmh/java/com/endoflineblog/truffle/part_17/ClosureBenchmark.java)
that uses a simple countdown function,
similar to what we used for exceptions in
[part 15](../part-15/src/jmh/java/com/endoflineblog/truffle/part_15/CountdownBenchmark.java).
The benchmark has multiple variants,
comparing the baseline performance without closures,
and then with closures,
both for nested and anonymous functions:

```shell-session
Benchmark                                 Mode  Cnt    Score    Error  Units
ClosureBenchmark.count_down_baseline_ezs  avgt    5  674.853 ± 15.058  us/op
ClosureBenchmark.count_down_baseline_js   avgt    5  720.268 ±  6.003  us/op
ClosureBenchmark.count_down_closure_ezs   avgt    5  725.877 ± 24.909  us/op
ClosureBenchmark.count_down_closure_js    avgt    5  795.591 ± 20.808  us/op
ClosureBenchmark.count_down_lambda_ezs    avgt    5  718.803 ± 42.839  us/op
ClosureBenchmark.count_down_lambda_js     avgt    5  708.936 ± 19.386  us/op
ClosureBenchmark.count_down_nested_ezs    avgt    5  715.177 ± 14.053  us/op
ClosureBenchmark.count_down_nested_js     avgt    5  682.771 ± 30.691  us/op
```

The results for all four benchmarks are pretty much identical,
which means closures don't carry a performance penalty in Truffle.
