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

The `frameDescriptor` field is used to create the `FrameSlot`s
that actually index inside the `VirtualFrame`.
We need to provide it to the `RootNode` that we use for functions
(the [`StmtBlockRootNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/StmtBlockRootNode.java))
in order for the frame created by the `CallTarget` wrapping the `RootNode`
to have the appropriate size.

Parsing is further made more complicated because of JavaScript's
[variable hoisting](https://developer.mozilla.org/en-US/docs/Glossary/Hoisting).
We have to do two passes over a given block of statements.
In the first pass, we only gather the declarations,
discarding the initializers of variables.
In the second pass, we gather the remaining statements,
and turn every variable declaration into an assignment expression.

## Local & global variable declarations

The [`LocalVarDeclStmtNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/stmts/LocalVarDeclStmtNode.java)
implements local variables by writing them into the `VirtualFrame`
argument of the `execute()` method.

The [`GlobalVarDeclStmtNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/stmts/GlobalVarDeclStmtNode.java)
implements global variables by saving them in the
[`GlobalScopeObject`](src/main/java/com/endoflineblog/truffle/part_07/runtime/GlobalScopeObject.java),
which is very similar to the class with the same name from the previous chapters.

The only difference in this part,
with both of these variable declaration implementations,
is that we need to write a special "dummy" value that signifies the given variable was used before being initialized --
this is a requirement to correctly implement hoisting.
Those dummy values will then be treated specially by the reference expressions.

The interesting part in `GlobalVarDeclStmtNode`
is getting a reference to the language's context in a non-specialization method,
which use `@CachedContext`, as we've seen since [part 5](../part-05).
Here, we use the `currentLanguageContext()` method from the
[common superclass of all nodes, `EasyScriptNode`](src/main/java/com/endoflineblog/truffle/part_07/nodes/EasyScriptNode.java),
which uses a static `ContextReference` field defined in the
[context class](src/main/java/com/endoflineblog/truffle/part_07/EasyScriptLanguageContext.java),
and surfaced using the `get` method.

## Assignment expressions

An assignment to a local variable is implemented by the
[`LocalVarAssignmentExprNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/LocalVarAssignmentExprNode.java) --
we note down the type of variable in its `FrameSlot`.

The assignment to global variables is implemented by the
[`GlobalVarAssignmentExprNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/GlobalVarAssignmentExprNode.java),
which is [unchanged from the last part](../part-06/src/main/java/com/endoflineblog/truffle/part_06/nodes/exprs/GlobalVarAssignmentExprNode.java).

An assignment to the parameter of a function is implemented by the
[`WriteFunctionArgExprNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/functions/WriteFunctionArgExprNode.java).
There's an interesting detail here:
as we saw in the
[previous part](../part-06/src/main/java/com/endoflineblog/truffle/part_06/nodes/exprs/functions/ReadFunctionArgExprNode.java),
it's legal to call a function with JavaScript with less arguments than it declares --
the remaining ones are simply passed as `undefined`.
But how does that work if the function writes to an argument that wasn't passed?
The `arguments` array in the frame might not have enough space to perform the write.

To solve this, we actually change the
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
wrap it in a `CallTarget`,
and finally wrap that in the same `FunctionObject` that we've seen
[in the last article](../part-06/src/main/java/com/endoflineblog/truffle/part_06/runtime/FunctionObject.java)
used for the built-in functions --
except we pass the number of parameters the function takes,
in order for the dispatch code mentioned above to be able to extend the arguments with `undefined`
if the calling code passed fewer of them than the function declares.

We need a reference to the `TruffleLanguage`
instance from an `execute()` method in `FuncDeclStmtNode`
to create the `RootNode`,
so we use a similar trick in this class as we employed in `GlobalVarDeclStmtNode`,
but this time using the `currentTruffleLanguage()` method from
[`EasyScriptNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/EasyScriptNode.java)
which uses the `LanguageReference` static field in the
[`EasyScriptTruffleLanguage` class](src/main/java/com/endoflineblog/truffle/part_07/EasyScriptTruffleLanguage.java).

## Reference expressions

A reference to a local variable is implemented by the
[`LocalVarReferenceExprNode` class](src/main/java/com/endoflineblog/truffle/part_07/nodes/exprs/LocalVarReferenceExprNode.java).
It uses the types `LocalVarAssignmentExprNode`
saved in the `FrameSlot` for specializations with the `guards` attribute that we've seen first in the previous chapter.

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