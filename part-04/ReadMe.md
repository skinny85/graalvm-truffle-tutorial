# Part 4 - parsing, and the `TruffleLanguage` class

## Parsing

Parsing is the process of turning the text of a program into its
Abstract Syntax Tree representation.

Truffle does not have a built-in way to perform parsing,
and instead allows you to use any library you want for this task.

This article series uses [ANTLR](https://www.antlr.org).
The way it's set up is that we have a
[grammar file, `EasyScript.g4`](src/main/antlr/com/endoflineblog/truffle/part_04/EasyScript.g4),
that represents a language for adding integer and `double` literals from [part 3](../part-03).
That grammar file is read by the ANTLR Gradle plugin,
enabled in the [`build.gradle` file of the module](build.gradle),
to generate classes that perform the parsing.
Those generated classes are then used by the
[`EasyScriptTruffleParser` class](src/main/java/com/endoflineblog/truffle/part_04/EasyScriptTruffleParser.java)
to turn the ANTLR parse tree into the Truffle AST.

There is a [unit test](src/test/java/com/endoflineblog/truffle/part_04/ParsingTest.java)
confirming this works as expected.

## `TruffleLanguage`

`TruffleLanguage` is an abstract class that you can extend to make your language part of the GraalVM polyglot API.
Our implementation is in the
[`EasyScriptTruffleLanguage` class](src/main/java/com/endoflineblog/truffle/part_04/EasyScriptTruffleLanguage.java).

[Here](src/test/java/com/endoflineblog/truffle/part_04/PolyglotTest.java)
is a unit test showing how can you invoke EasyScript using GraalVM's polyglot API.
