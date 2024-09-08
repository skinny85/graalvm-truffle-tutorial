# Part 15 - exceptions

In this part of the series,
we add exception handling to EasyScript.
As part of that, we will also learn about a few new Truffle concepts,
like the [`SourceSection` class](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/source/SourceSection.html),
the [`TruffleStackTrace` class](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/TruffleStackTrace.html),
and [`TruffleStackTraceElement` class](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/TruffleStackTraceElement.html).

## Grammar

In order to support exception handling,
we need to introduce two new types of statements to the
[ANTLR grammar](src/main/antlr/com/endoflineblog/truffle/part_15/parsing/antlr/EasyScript.g4) for EasyScript.
The first one is the `throw` statement,
which allows raising an exception.
The second is the `try` statement,
used for handling exceptions,
and which comes in two flavors:
one with the `catch` statement followed by an optional `finally` statement
(which executes regardless whether an option was thrown in the `try` block, or not),
or one where `catch` is missing,
in which case the `finally` part becomes required --
for that reason, we have two grammar rules for the `try` statement,
covering both those scenarios.

## Parsing

[Our parser](src/main/java/com/endoflineblog/truffle/part_15/parsing/EasyScriptTruffleParser.java)
needs a few changes.
It now takes an instance of the [`ShapesAndPrototypes` class](src/main/java/com/endoflineblog/truffle/part_15/common/ShapesAndPrototypes.java)
as an argument, as we add additional built-in classes beyond just `Object` to the language that represent errors.

We also need to save the `Source` instance that we are parsing as field,
as we will need it to construct
[`SourceSection` instances](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/source/SourceSection.html)
for our Nodes so that the stack trace of the exception is filled correctly (see below),
so we change the way parsing entrypoint works to move it inside an instance method of the parser,
and pass the `source` in the constructor
(instead of doing it all in the static factory method, like in previous parts).

## The `throw` statement

The implementation of the [`throw` statement](src/main/java/com/endoflineblog/truffle/part_15/nodes/stmts/exceptions/ThrowStmtNode.java)
uses two specializations. The first is when the thrown value is an object,
in which case we formulate the message by reading its `name` and `message` properties,
and we also save the stack trace on that object in the [`stack` property](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Error/stack)
(note that in JavaScript, that property is filled when creating an instance of `Error`,
but that makes the code more complicated, so I decided to simplify in EasyScript).
The second specialization covers the case when a non-object value is thrown
(in JavaScript, you can throw any value, not only a subclass of `Error`,
unlike in many other languages).
For both specializations, we use the existing
[`EasyScriptException` class](src/main/java/com/endoflineblog/truffle/part_15/exceptions/EasyScriptException.java),
just with a new field, `value`, that represents the thrown object,
which we will need in the implementation of the `try` statement.

The `SourceSection` instances used to formulate the stack trace are implemented in the parser by referencing
the position of the tokens in the string as saved by [ANTLR](https://www.antlr.org),
and then returned in the overridden [`getSourceSection()` method from `Node`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/nodes/Node.html#getSourceSection()).
While in theory, we could override that method in all of our Nodes,
to get a good enough stack trace, we just do it for
[expression statements](src/main/java/com/endoflineblog/truffle/part_15/nodes/stmts/ExprStmtNode.java),
[return statements](src/main/java/com/endoflineblog/truffle/part_15/nodes/stmts/controlflow/ReturnStmtNode.java),
and [`throw` statements](src/main/java/com/endoflineblog/truffle/part_15/nodes/stmts/exceptions/ThrowStmtNode.java).

We also override the [`getName()` method of `RootNode`](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/nodes/RootNode.html#getName())
inside the existing [`StmtBlockRootNode` class](src/main/java/com/endoflineblog/truffle/part_15/nodes/root/StmtBlockRootNode.java)
to return the name of the function
(passed from the [`FuncDeclStmtNode` class](src/main/java/com/endoflineblog/truffle/part_15/nodes/stmts/variables/FuncDeclStmtNode.java)),
or the `":program"` string for the top-level script, set in
[`EasyScriptTruffleLanguage`](src/main/java/com/endoflineblog/truffle/part_15/EasyScriptTruffleLanguage.java).

## The `try` statement

In the [`try` statement](src/main/java/com/endoflineblog/truffle/part_15/nodes/stmts/exceptions/TryStmtNode.java),
we catch `EasyScriptException`, and assign the thrown value to the local variable whose name was provided in the `catch` statement,
using the new `value` field of `EasyScriptException` that we populated in the `throw` statement.
We create the new local variable during parsing, and pass its integer index to `TryStmtNode` when we create it.

Since Java has the same `try`, `catch` and `finally` language constructs as JavaScript,
it's very easy to use them in our interpreter,
we just have to first check whether we're dealing with `try-catch` with an optional `finally` case,
or the `try-finally` case, since we don't want our Java code to catch the exception if the EasyScript code didn't
(since this is a compile-time decision in each Node,
when JIT-compiling a specific instance, the `if` will be eliminated,
and only one of its branches included).

## Handling built-in errors

The final piece of the puzzle is using the same exception mechanism for built-in errors,
like writing a negative array length.
JavaScript has an entire [hierarchy of error classes](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Error#error_types).
We only implement [`Error`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Error)
and throw [`TypeError`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/TypeError)
when reading a property of `undefined` in EasyScript as illustrative examples.

In order to allow EasyScript code access to these new classes,
we initialize them inside `EasyScriptTruffleLanguage`.
In particular, each of these classes has a constructor that takes one argument,
and then writes that argument to the `message` property of `this`,
and also writes the `name` property of `this` with a value equal to the class's name
(we've seen those two properties referenced in the `throw` statement specialization for an object).
Since we need to populate these built-in classes during language initialization,
before we can parse any code, we create these constructors "by hand",
by creating instances of the appropriate AST Nodes.

Then, we change the [`CommonReadPropertyNode` class](src/main/java/com/endoflineblog/truffle/part_15/nodes/exprs/properties/CommonReadPropertyNode.java)
to create an instance of `TypeError` when reading a property of `undefined`.
The tricky part is creating an instance of that class from Java interpreter code --
like we mentioned above, that class has a specific constructor in EasyScript,
but there's no easy way to invoke that constructor from Java.
So, we use a small trick - we introduce a new
[subclass of `JavaScriptObject`, `ErrorJavaScriptObject`](src/main/java/com/endoflineblog/truffle/part_15/runtime/ErrorJavaScriptObject.java),
that essentially re-implements that constructor logic, but this time in Java,
and we make sure to pass an instance of `ErrorJavaScriptObject` to `EasyScriptException`
that we throw in `CommonReadPropertyNode` when a property of `undefined` is read.

## Benchmark

Even though exceptions are quite slow
(for example, the stack trace gathering we saw above is quite costly,
and annotated with the [`@TruffleBoundary` annotation](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/CompilerDirectives.TruffleBoundary.html)),
and are thus not a good fit for performance-critical code,
we still introduce a
[simple benchmark](src/jmh/java/com/endoflineblog/truffle/part_15/CountdownBenchmark.java)
that loops a given amount of times,
and then throws an exception to terminate the loop.

Here are the results when running it on my laptop:

```
Benchmark                                                Mode  Cnt    Score    Error  Units
CountdownBenchmark.count_down_with_exception_ezs         avgt    5  921.898 ± 32.561  us/op
CountdownBenchmark.count_down_with_exception_js          avgt    5  928.523 ±  8.294  us/op
```

As we can see, both EasyScript and the GraalVM JavaScript implementation have basically identical performance,
which means we at least didn't introduce some obvious inefficiency to EasyScript.

---

In addition to the benchmark, there are some
[unit tests](src/test/java/com/endoflineblog/truffle/part_15/ExceptionsTest.java)
that validate the exception handling functionality works as expected.

Note that we use the [`Source` class](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/source/Source.html),
and the [`Context.eval(Source)` method](https://www.graalvm.org/truffle/javadoc/org/graalvm/polyglot/Context.html#eval(org.graalvm.polyglot.Source))
in many of the tests over the
[`Context.eval(String, String)` method](https://www.graalvm.org/truffle/javadoc/org/graalvm/polyglot/Context.html#eval(java.lang.String,java.lang.CharSequence))
which we predominantly used for tests in the previous chapters,
as using `Context.eval(Source)`, and putting the tested EasyScript code in a separate file,
naturally results in meaningful source sections in stack traces,
while we would have to add newline characters explicitly to the string literal source code passed to
`Context.eval(String, String)`, which is cumbersome.
