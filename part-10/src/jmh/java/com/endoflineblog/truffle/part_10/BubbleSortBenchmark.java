package com.endoflineblog.truffle.part_10;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BubbleSortBenchmark extends TruffleBenchmark {
    private static final int arraySize = 1_000;
    private static final int[] descendinglySortedArray;
    private static final String JS_BUBBLE_SORT;

    static {
        descendinglySortedArray = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            descendinglySortedArray[i] = arraySize - i;
//            descendinglySortedArray[i] = i;
        }
        JS_BUBBLE_SORT = "" +
                "function bubbleSort() { " +
                "    const array = " + Arrays.stream(descendinglySortedArray)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]")) + "; " +
                "    for (var i = 0; i < " + arraySize + " - 1; i = i + 1) { " +
                "        for (var j = 0; j < " + arraySize + " - 1 - i; j = j + 1) { " +
                "            if (array[j] > array[j + 1]) { " +
                "                var tmp = array[j]; " +
                "                array[j] = array[j + 1]; " +
                "                array[j + 1] = tmp; " +
                "            } " +
                "        } " +
                "    } " +
                "    return array; " +
                "} " +
                "bubbleSort(); ";
    }

    @Benchmark
    public int[] java() {
        int[] array = descendinglySortedArray.clone();
        bubbleSort(array);
        return array;
    }

    @Benchmark
    public long ezs() {
        return this.truffleContext.eval("ezs", JS_BUBBLE_SORT).getArraySize();
    }

    @Fork(jvmArgsAppend = "-Dgraalvm.locatorDisabled=false")
    @Benchmark
    public long js() {
        return this.truffleContext.eval("js", JS_BUBBLE_SORT).getArraySize();
    }

    static void bubbleSort(int[] array) {
        for (var i = 0; i < array.length - 1; i = i + 1) {
            for (var j = 0; j < array.length - 1 - i; j = j + 1) {
                if (array[j] > array[j + 1]) {
                    // swap j and j+1
                    var tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                }
            }
        }
    }
}
