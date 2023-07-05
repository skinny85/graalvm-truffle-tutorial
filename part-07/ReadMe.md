# Part 7 - function definitions

In this part of the series,
we add support for function definitions to our language.

We make a few simplifications in this part that we will get rid of in later versions of EasyScript:

1. We don't implement a `return` statement --
  the function will return the value of the last statement.
2. We don't allow nested functions
  (a function defined inside another function).
3. We don't support the magical
  [`arguments` variable](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/arguments)
  inside the function definition.

## Grammar

The grammar is in the [`EasyScript.g4` file](src/main/antlr/com/endoflineblog/truffle/part_07/EasyScript.g4).
The only change compared to the
[grammar from part 6](../part-06/src/main/antlr/com/endoflineblog/truffle/part_06/EasyScript.g4)
is a new type of Statement,
the function declaration statement (`FuncDeclStmt`),
and its accompanying production for arguments (`func_args`).

## Parsing

Our [parsing class](src/main/java/com/endoflineblog/truffle/part_07/EasyScriptTruffleParser.java)
needs major changes.
While the entrypoint will still be a static `parse()` method on the class,
the implementation will now be stateful --
we need to record the names of the function arguments and the local variables we encounter inside a function definition.
We do that in the `functionLocals` field.
To distinguish between function arguments and local variables of functions,
we introduce an abstract class, `FrameMember`,
with two concrete subclasses, `FunctionArgument` and `LocalVariable`.

The `frameDescriptor` field is used to create the frame slots for the local variables
that actually index into the `VirtualFrame`.
Historically, those indexes were a separate class called `FrameSlot`,
but starting with GraalVM version `22`,
that class has been removed,
and slots are now indexed with integers,
the same way function arguments are.
We need to provide the `FrameDescriptor` obtained from
the Builder in the `frameDescriptor` field to the `RootNode` that we use for functions
(the [`StmtBlockRootNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/StmtBlockRootNode.java))
in order for the `VirtualFrame` created by the `CallTarget` wrapping the `RootNode`
to have the appropriate size.

Since frame slots are created at parse time,
we don't need an equivalent of `GlobalVarDeclStmtNode` for local variables.
Because of that, we transform a local variable declaration into an assignment expression when parsing.

Because it's legal to invoke a function in JavaScript before it's declared,
we have to do two passes over a given block of statements.
In the first pass, we only process the function declarations;
in the second pass, we handle the remaining, non-function declaration statements.

## Assignment expressions

An assignment to a local variable is implemented by the
[`LocalVarAssignmentExprNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/LocalVarAssignmentExprNode.java) --
we save the type of the variable in the frame descriptor of a given frame.
The assignment to global variables is implemented by the
[`GlobalVarAssignmentExprNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/GlobalVarAssignmentExprNode.java),
which is [unchanged from the last part](../part-06/src/main/java/com/endoflineblog/truffle/part_06/nodes/exprs/GlobalVarAssignmentExprNode.java).

An assignment to the parameter of a function is implemented by the
[`WriteFunctionArgExprNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/functions/WriteFunctionArgExprNode.java).
There's an interesting detail here:
as we saw in the
[previous part](../part-06/src/main/java/com/endoflineblog/truffle/part_06/nodes/exprs/functions/ReadFunctionArgExprNode.java),
it's legal to call a function with JavaScript with fewer arguments than it declares --
the remaining ones are simply passed as `undefined`.
But in that case, the `arguments` array in the frame might not have enough space to perform a write to the given argument.

To solve this issue, we actually change the
[dispatch code](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/functions/FunctionDispatchNode.java)
from the
[last part](../part-06/src/main/java/com/endoflineblog/truffle/part_06/nodes/exprs/functions/FunctionDispatchNode.java)
to extend the arguments array with `undefined`s
if the call expression had less arguments than the function takes.
This way, we are guaranteed we will always have enough `arguments`
in the frame to store the assignments to all arguments of the function,
even if that argument was never passed.

## Function declarations

A function declaration is implemented by the
[`FuncDeclStmtNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/stmts/FuncDeclStmtNode.java).
We create a new `StmtBlockRootNode`,
get a `CallTarget` from it,
and finally wrap that in the same `FunctionObject` that we've seen
[in the last article](../part-06/src/main/java/com/endoflineblog/truffle/part_06/runtime/FunctionObject.java)
used for the built-in functions --
except we pass the number of parameters the function takes,
in order for the dispatch code mentioned above to be able to extend the arguments with `undefined`
if the calling code passed fewer of them than the function declares.

We need a reference to the `TruffleLanguage`
instance from an `execute()` method in `FuncDeclStmtNode`
to create the `RootNode`,
so we use a similar trick in this class as we employed in
[`GlobalVarDeclStmtNode` in part 5](../part-05/src/main/java/com/endoflineblog/truffle/part_05/nodes/stmts/GlobalVarDeclStmtNode.java),
but this time using the `currentTruffleLanguage()` method from
[`EasyScriptNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/EasyScriptNode.java)
which uses the `LanguageReference` static field in the
[`EasyScriptTruffleLanguage` class](src/main/java/com/endoflineblog/truffle/part_07/EasyScriptTruffleLanguage.java).

## Reference expressions

A reference to a local variable is implemented by the
[`LocalVarReferenceExprNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/LocalVarReferenceExprNode.java).
It uses the types `LocalVarAssignmentExprNode`
saved in the frame descriptor for specializations with the `guards` attribute that we've seen first in the previous chapter.

Referencing global variables
[is unchanged](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/GlobalVarReferenceExprNode.java)
from the [last chapter](../part-06/src/main/java/com/endoflineblog/truffle/part_06/nodes/exprs/GlobalVarReferenceExprNode.java).
Referencing a function  argument is
[almost identical](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/functions/ReadFunctionArgExprNode.java),
with the slight simplification that we no longer have to check whether the
`arguments` array is large enough
(the changes in the calling code mentioned above for assigning function parameters make sure it is).

---

There is a [unit test](src/test/java/com/endoflineblog/truffle/part_07/FunctionDefinitionsTest.java)
exercising a few common scenarios with function definitions.
