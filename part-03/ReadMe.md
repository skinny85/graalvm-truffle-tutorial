# Part 3 - specializations using Truffle DSL, `TypeSystem`

In this part,
we will use the [Truffle DSL](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/dsl/package-summary.html)
to achieve the same result as we did in part 2,
but with a fraction of the code.

The interesting part is the [`AdditionNode` class](src/main/java/com/endoflineblog/truffle/part_03/AdditionNode.java).
The Truffle DSL annotation processor will generate a new class, `AdditionNodeGen`,
extending `AdditionNode`, that actually implements the `execute*()` methods.
You create instances of `AdditionNodeGen` by calling its static factory `create()` method,
and providing the two child nodes.

See the tests in [`ExecuteNodesDslTest`](src/test/java/com/endoflineblog/truffle/part_03/ExecuteNodesDslTest.java)
on how to use the generated `AdditonNodeGen` class.

## `@TypeSystem`

A TypeSystem is sometimes required when using specializations,
to let the DSL know that it should treat certain types differently --
by default, it implements type checking with `instanceof`,
and type conversions just with Java casts.
Since our EasyScript implementation allows mixing of `int`s and `double`s,
we need to inform the DSL of that fact.
We do that in the [`EasyScriptTypeSystem` class](src/main/java/com/endoflineblog/truffle/part_03/EasyScriptTypeSystem.java),
which is used in the [`EasyScriptNode` superclass](src/main/java/com/endoflineblog/truffle/part_03/EasyScriptNode.java)
by using the `@TypeSystemReference` annotation
(the type system is inherited by subclasses,
so this way all EasyScript Nodes will have it without having to repeat it for each class).

Feel free to comment out the `@TypeSystemReference` line from `EasyScriptNode` --
you should see 2 out of 3 tests in `ExecutesNodeDslTest` fail.
