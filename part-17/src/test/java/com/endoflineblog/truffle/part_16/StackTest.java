package com.endoflineblog.truffle.part_16;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StackTest {
    @Test
    void stack_pushes_elements_to_end_index() {
        var stack = new Stack<Integer>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(1, stack.get(0));
        assertEquals(3, stack.peek());
        assertEquals(1, stack.firstElement());
        assertEquals(3, stack.lastElement());

        // default iteration order is bottom-to-top
        int i = 0;
        for (int v : stack) {
            assertEquals(++i, v);
        }
        assertEquals(3, i);

        int k = 3;
        for (var iter = stack.listIterator(stack.size()); iter.hasPrevious();) {
            assertEquals(k--, iter.previous());
        }
        assertEquals(0, k);
    }

    @Test
    void ArrayDeque_pushes_elements_to_start_index() {
        Deque<Integer> deque = new ArrayDeque<>();
        deque.push(1);
        deque.push(2);
        deque.push(3);

        assertEquals(3, deque.getFirst());
        assertEquals(1, deque.getLast());
        assertEquals(3, deque.peek());

        // default iteration order is top-to-bottom
        int k = 3;
        for (int v : deque) {
            assertEquals(k--, v);
        }
        assertEquals(0, k);
    }
}
