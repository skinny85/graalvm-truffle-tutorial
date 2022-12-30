# Part 1 - `Node`, `RootNode`, `CallTarget`

In this first part,
EasyScript will be extremely simple:
it will only allow addition of integer literals
(so, expressions like `1 + 2 + 3`).

We'll need the following Truffle APIs to accomplish this:

## `com.oracle.truffle.api.nodes.Node`

The `Node` class is the abstract superclass of all nodes in your AST interpreter.
It doesn't define any abstract "interpret"
method that you're supposed to override;
instead, you're expected to define it yourself
(the reason Truffle does it that way are specializations,
which are language-specific;
we cover them in later parts of the tutorial).
Because of this,
you almost always define your own abstract superclass of all nodes in your language.
For EasyScript, that's the
[`EasyScriptNode` class](src/main/java/com/endoflineblog/truffle/part_01/EasyScriptNode.java).

We also define two concrete subclasses of `EasyScriptNode`,
[`IntLiteralNode`](src/main/java/com/endoflineblog/truffle/part_01/IntLiteralNode.java)
and [`AdditionNode`](src/main/java/com/endoflineblog/truffle/part_01/AdditionNode.java),
which represent integer literals and the plus operator,
respectively.

## `com.oracle.truffle.api.nodes.RootNode`

Each Truffle AST needs to be anchored in a `RootNode` to be executable.
This is another abstract class that you need to extend,
and, unlike `Node`,
it does define an abstract `execute` method that you need to override.
Our implementation is the
[`EasyScriptRootNode` class](src/main/java/com/endoflineblog/truffle/part_01/EasyScriptRootNode.java),
and its `execute` method simply delegates to the `EasyScriptNode`'s `executeInt`
that it gets passed through the constructor.

## `com.oracle.truffle.api.CallTarget`

Since the `execute` method in `RootNode` takes a Truffle `VirtualFrame`
as its argument,
we need one more class to execute the AST: a `CallTarget`.
This is an interface, not an abstract class.
Historically, you created instances of it through a static factory method
`createCallTarget()` of the `TruffleRuntime` class,
which is a singleton you acquired using the `getRuntime()`
method of the `Truffle` class.
However, since version `22` of GraalVM,
that API has been removed,
and now, you acquire `CallTarget`s directly from `RootNode`s,
by calling their `getCallTarget()` method.
In our case, the root node is `EasyScriptRootNode`.

With a `CallTarget` reference,
we can finally invoke its `calll` method with no arguments.
That will create a `VirtualFrame` instance,
and call the `execute` method of the `RootNode` it got passed,
which in turn calls the `executeInt` method of `EasyScriptNode`.

All of this is implemented in a
[simple unit test](src/test/java/com/endoflineblog/truffle/part_01/ExecuteNodesTest.java).
