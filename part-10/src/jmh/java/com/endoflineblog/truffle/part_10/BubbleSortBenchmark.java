package com.endoflineblog.truffle.part_10;

import org.openjdk.jmh.annotations.Benchmark;

public class BubbleSortBenchmark extends TruffleBenchmark {
    private static final int arraySize = 1_000;
    private static final int[] descendinglySortedArray;

    static {
        descendinglySortedArray = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            descendinglySortedArray[i] = arraySize - i;
//            descendinglySortedArray[i] = i;
        }
    }

    @Benchmark
    public int[] java() {
        int[] array = descendinglySortedArray.clone();
        bubbleSort(array);
        return array;
    }

    static void bubbleSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    // swap j and j+1
                    int tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                }
            }
        }
    }
}
