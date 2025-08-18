package adt;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayStack<T> implements StackInterface<T>, Iterable<T> {

    private T[] stackArray;
    private int top;
    private int capacity;

    private static final int INITIAL_CAPACITY = 5;

    public ArrayStack() {
        stackArray = (T[]) new Object[INITIAL_CAPACITY];
        top = -1;
        capacity = INITIAL_CAPACITY;
    }

    @Override
    public void push(T item) {
        if (isFull()) {
            resize();
        }
        stackArray[++top] = item;
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T item = stackArray[top];
        stackArray[top--] = null;
        return item;
    }

    @Override
    public boolean isEmpty() {
        return top == -1;
    }

    private boolean isFull() {
        return top == capacity - 1;
    }

    private void resize() {
        capacity = capacity * 2;
        T[] newArray = (T[]) new Object[capacity];
        System.arraycopy(stackArray, 0, newArray, 0, top + 1); // Copy existing elements
        stackArray = newArray;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return stackArray[top];
    }

    @Override
    public int size() {
        return top + 1;
    }

    @Override
    public void clear() {
        for (int i = 0; i <= top; i++) {
            stackArray[i] = null;
        }
        top = -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayStackIterator();
    }

    private class ArrayStackIterator implements Iterator<T> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex <= top;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return stackArray[currentIndex++];
        }
    }
}