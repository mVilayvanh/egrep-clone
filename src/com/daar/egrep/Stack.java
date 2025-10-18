package com.daar.egrep;

import java.util.*;

public class Stack<E> implements Iterable<E> {

    private final Deque<E> stack = new ArrayDeque<>();
    private final Set<E> set = new HashSet<>();

    public void push(E e) {
        if (set.add(e)) {
            stack.push(e);
        }
    }

    public E pop() {
        var e = stack.pop();
        set.remove(e);
        return e;
    }

    public E peek() {
        return stack.peek();
    }

    public boolean contains(E e) {
        return set.contains(e);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }


    @Override
    public Iterator<E> iterator() {
        return stack.iterator();
    }

    @Override
    public String toString() {
        return stack.toString();
    }
}
