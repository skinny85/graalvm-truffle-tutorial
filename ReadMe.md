# GraalVM Truffle tutorial

This repository contains the code for a tutorial on the
[Truffle language implementation framework](https://github.com/oracle/graal/blob/master/truffle/docs/README.md)
that I've [written for my blog](http://endoflineblog.com/graal-truffle-tutorial-part-0-what-is-truffle).
It focuses on implementing a language I call EasyScript,
which is a very simplified subset of JavaScript.

The repository is divided into multiple parts,
each corresponding to a part of the blog article.
Each part focuses on explaining a small set of Truffle capabilities,
and builds on top of the previous parts by adding more features to the EasyScript language implementation.

Each part is a separate [Gradle submodule](https://docs.gradle.org/current/userguide/multi_project_builds.html),
and so gets built when you build the top-level project.

## Setup

To build and execute this project, you need a GraalVM installation on your local machine.
The free Community Edition works fine if you don't have the paid Enterprise Edition.
You can download it from here: https://github.com/graalvm/graalvm-ce-builds/releases.
This repository uses Java 11 features,
so make sure to download a version for Java 11.

Once you've downloaded the correct archive for your operating system and extracted it somewhere on your machine,
you need to set the `JAVA_HOME`
environment variable to point to the directory containing the uncompressed contents:

```shell script
$ export JAVA_HOME=/path/to/extracted/archive
```

You can verify the installation works by executing the `java`
command using `JAVA_HOME`:

```shell script
$ $JAVA_HOME/bin/java -version

openjdk version "11.0.8" 2020-07-14
OpenJDK Runtime Environment GraalVM CE 20.2.0 (build 11.0.8+10-jvmci-20.2-b03)
OpenJDK 64-Bit Server VM GraalVM CE 20.2.0 (build 11.0.8+10-jvmci-20.2-b03, mixed mode, sharing)
```

## Building

Once you have GraalVM installed,
you can build the project -
it uses [Gradle](https://gradle.org)
as its build system:

```shell script
$ ./gradlew build
```

## Table of contents

* [Part 1 - `Node`, `RootNode`, `CallTarget`](part-01/ReadMe.md)
* [Part 2 - introduction to specializations](part-02/ReadMe.md)
