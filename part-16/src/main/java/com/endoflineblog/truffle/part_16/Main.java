package com.endoflineblog.truffle.part_16;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;

/**
 * A main class that evaluates the JavaScript program for calculating Fibonacci numbers.
 * It uses the GraalVM Polyglot API to start a debugger on port 4242.
 * The code will suspend execution, and wait for the debugger to connect.
 * The program will print a link that you can open in Google Chrome to start the debugger.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Source source = Source
                .newBuilder("ezs", new File("src/main/resources/fibonacci.js"))
                .build();

        try (Context context = Context
                .newBuilder()
                .option("inspect", "4242")
                .build()) {
            Value result = context.eval(source);
            System.out.println(result.toString());
        }
    }
}
