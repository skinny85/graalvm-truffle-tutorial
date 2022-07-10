# Part 8 - conditionals, loops, control flow

In this part of the series,
we add support for comparison operators,
`if` statements, loops, and `return`, `break` and `continue` statements.

## Grammar

The grammar is in the [`EasyScript.g4` file](src/main/antlr/com/endoflineblog/truffle/part_08/parsing/antlr/EasyScript.g4).
The changes compared to the
[grammar from part 7](../part-07/src/main/antlr/com/endoflineblog/truffle/part_07/EasyScript.g4)
are the last eight productions for statements,
and two new for expressions
(`EqNotEqExpr2` and `ComparisonExpr3`).

## Parsing

Our [parsing class](src/main/java/com/endoflineblog/truffle/part_08/parsing/EasyScriptTruffleParser.java)
again needs major changes.

We need to expand the state we're tracking as part of parsing.
We will need four fields instead of just two:
1. Whether we're parsing the top-level scope,
   a nested scope of the top-level,
   or a function.
2. The `FrameDescriptor` -- in this part, the top-level scope can include local variables too
   (they are not exclusive to function definitions).
3. A Stack of Maps that contain the local variables in each scope.
   When we enter a new scope, we push a new Map onto this Stack;
   when we leave a scope, we pop a Map off the Stack.
4. A counter that we keep incrementing after every usage that generates unique `FrameSlot`
   names by combining it with the name of the variable
   (because of nested scopes, variable names are no longer guaranteed to be unique in a given `FrameDescriptor`).

When parsing a new scope
(which happens when entering a block of statements,
a function declaration, or a `for` loop),
we save the previous state, change it appropriately for the element we are parsing,
perform the parsing, and then set it back to the saved state.

We use the [`BlockStmtNode` class](src/main/java/com/endoflineblog/truffle/part_08/nodes/stmts/blocks/BlockStmtNode.java)
to represent a block of statements, which is unchanged from the
[last chapter](../part-07/src/main/java/com/endoflineblog/truffle/part_07/nodes/stmts/BlockStmtNode.java).

## Boolean expressions

In order to support comparison operators,
we need to add booleans to our language.
This means adding `boolean.class` to the
[`TypeSystem` hierarchy](src/main/java/com/endoflineblog/truffle/part_08/EasyScriptTypeSystem.java),
and adding a new `executeBool()` method to the
[superclass of all expression Nodes](src/main/java/com/endoflineblog/truffle/part_08/nodes/exprs/EasyScriptExprNode.java).
Note that unlike other `execute*()` methods that return primitives,
`executeBool()` doesn't throw `UnexpectedResultException`,
because in JavaScript, any value can be interpreted as a boolean
(only `false`, `0` and `undefined` are considered `false`,
all other values are `true`).

We will also have to accommodate booleans by adding specializations for them in
[local variable assignment](src/main/java/com/endoflineblog/truffle/part_08/nodes/exprs/variables/LocalVarAssignmentExprNode.java)
and [reference](src/main/java/com/endoflineblog/truffle/part_08/nodes/exprs/variables/LocalVarReferenceExprNode.java).

## Comparisons

We implement (strict)
[equality (`===`)](src/main/java/com/endoflineblog/truffle/part_08/nodes/exprs/comparisons/EqualityExprNode.java)
and [inequality (`!==`)](src/main/java/com/endoflineblog/truffle/part_08/nodes/exprs/comparisons/InequalityExprNode.java),
and also [lesser (`<`)](src/main/java/com/endoflineblog/truffle/part_08/nodes/exprs/comparisons/LesserExprNode.java),
[lesser or equal (`<=`)](src/main/java/com/endoflineblog/truffle/part_08/nodes/exprs/comparisons/LesserOrEqualExprNode.java),
[greater (`>`)](src/main/java/com/endoflineblog/truffle/part_08/nodes/exprs/comparisons/GreaterExprNode.java) and
[greater or equal (`>=`)](src/main/java/com/endoflineblog/truffle/part_08/nodes/exprs/comparisons/GreaterOrEqualExprNode.java).

We don't worry about edge cases with comparisons,
such as whether `true` is greater than `0`,
or if `undefined` is lesser or equal than `undefined` --
we simply return `false` in all of these cases.

## `if` statement

The implementation of `if` is
[very simple](src/main/java/com/endoflineblog/truffle/part_08/nodes/stmts/controlflow/IfStmtNode.java) --
we check the condition, and, if it’s satisfied, we execute the “then” part;
if it’s not, and an “else” part was provided, we execute that.

The interesting part of this Node is using the `ConditionProfile`
Truffle class that is used for profiling the condition.
Graal might use this information when doing JIT compilation --
for example, if it sees a given condition was never `true`,
it might generate different code for it.

## `return`, `break` and `continue`

Control flow statements like `return`, `break` and `continue`
are implemented in Truffle interpreters with exceptions.
We need a separate type of exception for each type of statement;
[each](src/main/java/com/endoflineblog/truffle/part_08/exceptions/ReturnException.java)
[of](src/main/java/com/endoflineblog/truffle/part_08/exceptions/BreakException.java)
[them](src/main/java/com/endoflineblog/truffle/part_08/exceptions/ContinueException.java)
needs to extend Truffle's `ControlFlowException`.

Then, the actual statements Nodes
[are](src/main/java/com/endoflineblog/truffle/part_08/nodes/stmts/controlflow/ReturnStmtNode.java)
[extremely](src/main/java/com/endoflineblog/truffle/part_08/nodes/stmts/controlflow/BreakStmtNode.java)
[simple](src/main/java/com/endoflineblog/truffle/part_08/nodes/stmts/controlflow/ContinueStmtNode.java) --
they basically just throw the appropriate exception
(`return` first evaluating its expression).

The exceptions will be caught by other Nodes --
the ones for loops in case of `BreakException` and `ContinueException` (see below),
or by [the `UserFuncBodyStmtNode` class](src/main/java/com/endoflineblog/truffle/part_08/nodes/stmts/blocks/UserFuncBodyStmtNode.java)
that is used for the body of a user-defined function.

## Loops

Loops are implemented using a dedicated Truffle helper `LoopNode`.
With it, we simply implement a different interface, `RepeatingNode`,
and its `executeRepeating()` method.
In that method, we execute the body of the loop, once,
and then return a boolean from it indicating whether we should continue with the next iteration of the loop.
If we return `true`, `LoopNode` will call `executeRepating()` again,
if we return `false`, the loop will terminate.

We have to make sure to catch `BreakException` and `ContinueException`
to return `false` from `executeRepeating()` or continue with the loop, respectively.

We implement three types of loops:
[`while`](src/main/java/com/endoflineblog/truffle/part_08/nodes/stmts/loops/WhileStmtNode.java),
[`do-while`](src/main/java/com/endoflineblog/truffle/part_08/nodes/stmts/loops/DoWhileStmtNode.java)
and [`for`](src/main/java/com/endoflineblog/truffle/part_08/nodes/stmts/loops/ForStmtNode.java).

---

There is a [unit test](src/test/java/com/endoflineblog/truffle/part_08/ControlFlowTest.java)
exercising a few common scenarios with control flow statements.
