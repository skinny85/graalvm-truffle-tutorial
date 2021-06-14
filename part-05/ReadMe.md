# Part 5 - global variables

In this part of the series,
we add support for (global) variables to the language.

The grammar is in the [EasyScript.g4 file](src/main/antlr/com/endoflineblog/truffle/part_05/EasyScript.g4).
We add statements to our language --
parsing our program will now result in a list of statements --
and also expression precedence,
so that `a = 1 + 1` is parsed as `a = (1 + 1)`,
and not `(a = 1) + 1`.

Our [`TruffleLanguage` class](src/main/java/com/endoflineblog/truffle/part_05/EasyScriptTruffleLanguage.java)
creates a new `FrameDescriptor`,
and passes it to the [parser class](src/main/java/com/endoflineblog/truffle/part_05/EasyScriptTruffleParser.java).
The parser class first invokes ANTLR to get the parse tree,
and then uses the descriptor to create `FrameSlot`s
whenever it encounters variable declarations or variable references,
passing them to [`AssignmentExprNode`](src/main/java/com/endoflineblog/truffle/part_05/nodes/exprs/AssignmentExprNode.java)
and [`ReferenceExprNode`](src/main/java/com/endoflineblog/truffle/part_05/nodes/exprs/ReferenceExprNode.java).

Finally, the [`EasyScriptRootNode`](src/main/java/com/endoflineblog/truffle/part_05/nodes/EasyScriptRootNode.java)
receives a list of statements in its constructor,
along with the `EasyScriptTruffleLanguage` and `FrameDescriptor` instances
which it passes to the `RootNode` class with a `super()` call,
and executes the list,
returning the result of the last statement.

There is a [unit test](src/test/java/com/endoflineblog/truffle/part_05/ExecutingTest.java)
exercising the positive test case,
and the possible errors, like duplicate variable declarations,
and referencing an undeclared variable.
