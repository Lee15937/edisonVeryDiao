package adt;

import java.util.Iterator;

public class SortedLinkedList<T extends Comparable<T>> implements SortedLinkedListInterface<T>, Iterable<T> {

    private Node<T> head;

    private static class Node<T> {

        private T data;
        private Node<T> next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }
    }

    public SortedLinkedList() {
        head = null;
    }

    // Add in sorted order
    @Override
    public boolean add(T newEntry) {
        Node<T> newNode = new Node<>(newEntry);

        // Case 1: Empty list or insert at head
        if (head == null || newEntry.compareTo(head.getData()) < 0) {
            newNode.setNext(head);
            head = newNode;
            return true;
        }

        // Case 2: Insert in sorted order
        Node<T> current = head;
        while (current.getNext() != null
                && newEntry.compareTo(current.getNext().getData()) > 0) {
            current = current.getNext();
        }
        newNode.setNext(current.getNext());
        current.setNext(newNode);
        return true;
    }

    // Cancel/remove target entry (by equality)
    @Override
    public boolean cancel(T targetEntry) {
        if (head == null) {
            return false;
        }

        if (head.getData().equals(targetEntry)) {
            head = head.getNext();
            return true;
        }

        Node<T> current = head;
        while (current.getNext() != null) {
            if (current.getNext().getData().equals(targetEntry)) {
                current.setNext(current.getNext().getNext());
                return true;
            }
            current = current.getNext();
        }
        return false; // Not found
    }

    // Check if list contains target
    @Override
    public boolean contains(T targetEntry) {
        Node<T> current = head;
        while (current != null) {
            if (current.getData().equals(targetEntry)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public int size() {
        int count = 0;
        Node<T> current = head;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    }

    // Display (generic)
    @Override
    public void display() {
        Node<T> current = head;
        if (current == null) {
            System.out.println("List is empty.");
            return;
        }

        while (current != null) {
            System.out.println(current.getData());
            current = current.getNext();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                T data = current.getData();
                current = current.getNext();
                return data;
            }
        };

    }
}
