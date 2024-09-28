package com.endoflineblog.truffle.part_15;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Source source = Source
                .newBuilder("ezs", new File("src/main/resources/example.js"))
                .build();

        try (Context context = Context
                .newBuilder()
                .option("inspect", "4242")
                .option("inspect.Suspend", "true")
                .allowExperimentalOptions(true)
                .option("inspect.Initialization", "true")
                .build()) {
            Value result = context.eval(source);
            System.out.println(result.toString());
        }
    }
}
