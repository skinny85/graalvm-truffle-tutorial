package com.endoflineblog.truffle.part_15;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Source source = Source
                .newBuilder("ezs", new File("src/main/resources/fibonacci.js"))
//                .newBuilder("js", new File("src/main/resources/fibonacci.js"))
                .build();

        try (Context context = Context
                .newBuilder()
                .option("inspect", "4242")
                .build()) {
            Value result = context.eval(source);
            if (result.hasArrayElements()) {
                System.out.print("[");
                for (long i = 0; i < result.getArraySize(); i++) {
                    if (i != 0) {
                        System.out.print(", ");
                    }
                    System.out.print(result.getArrayElement(i));
                }
                System.out.println("]");
            } else {
                System.out.println(result.toString());
            }
        }
    }
}
