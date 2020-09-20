# GraalVM Truffle tutorial

This repository contains the code for a tutorial on the
[Truffle language implementation framework](https://github.com/oracle/graal/blob/master/truffle/docs/README.md)
that I've [written for my blog](http://endoflineblog.com/graal-truffle-tutorial-part-0-what-is-truffle).
It focuses on implementing a language I call EasyScript,
which is a very simplified subset of JavaScript.

## Setup

First of all, you need a GraalVM installation on your local machine.
The free Community Edition works fine if you don't have the paid Enterprise Edition.
You can download it from here: https://github.com/graalvm/graalvm-ce-builds/releases.
This repository uses Java 11 features,
so make sure to get a version for Java 11.

Once you've downloaded the correct archive for your operating system and extracted it somewhere on your machine,
you need to set the `JAVA_HOME`
environment variable to point to the directory containing the uncompressed contents:

```shell script
$ export JAVA_HOME=/path/to/extracted/archive
```

You can verify the installation works by executing the `java`
command using `JAVA_HOME`:

```shell script
$ $JAVA_HOME/bin/java

openjdk version "11.0.8" 2020-07-14
OpenJDK Runtime Environment GraalVM CE 20.2.0 (build 11.0.8+10-jvmci-20.2-b03)
OpenJDK 64-Bit Server VM GraalVM CE 20.2.0 (build 11.0.8+10-jvmci-20.2-b03, mixed mode, sharing)
```

After that, you can build the project -
it uses [Gradle](https://gradle.org)
as the build system:

```shell script
$ ./gradlew build
```
