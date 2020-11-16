# Part 2 - introduction to specializations

While the code in part 1 correctly handles addition of small numbers like 12 and 34,
it fails for large numbers like `Integer.MAX_VALUE`.
It can be seen in the [`OverflowTest` unit test](../part-01/src/test/java/com/endoflineblog/truffle/part_01/OverflowTest.java).

We can implement addition correctly,
without sacrificing performance by just replacing `int`s with `double`s everywhere,
by employing a technique called **specializations**.
The basic idea is that a Node deliberately decides to handle only a subset of possible inputs
(in our case, `int`s whose sum is between `Integer.MIN_VALUE` and `Integer.MAX_VALUE`).
When a Node in a state that handles only that subset is JIT-compiled,
it will generate very efficient machine code.

Of course, specializations are _speculative_ -
the moment the assumptions are no longer true
(for example, our Node gets passed `double` values,
or `int`s that overflow),
we have to invalidate the previously generated machine code,
and go back to interpreter code.
This is expressed in Truffle using Java exceptions.

A more thorough explanation of specializations can be found in my
[blog article](http://endoflineblog.com/graal-truffle-tutorial-part-2-introduction-to-specializations).

The interesting code implementing specializations is in the
[`AdditionNode` class](src/main/java/com/endoflineblog/truffle/part_02/AdditionNode.java).
Since we have to add `double` handling anyway for `int` overflow,
we also add a [`double` literal Node](src/main/java/com/endoflineblog/truffle/part_02/DoubleLiteralNode.java) to our language.

There is a [unit test](src/test/java/com/endoflineblog/truffle/part_02/ExecuteNodesTest.java)
that demonstrates that the new implementation handles integer overflow correctly
(by switching to `double`s).
