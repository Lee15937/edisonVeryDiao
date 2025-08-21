/**
 *
 * @author Choi Kah Wai
 */

package adt;

import java.util.ArrayList;
import java.util.List;


public class patientQueue<T> implements QueueInterface<T> {
    private Node<T> front;
    private Node<T> rear;

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) {
            this.data = data;
        }
    }

    @Override
    public void enqueue(T newEntry) {
        Node<T> newNode = new Node<>(newEntry);
        if (isEmpty()) {
            front = newNode;
        } else {
            rear.next = newNode;
        }
        rear = newNode;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) return null;
        T data = front.data;
        front = front.next;
        if (front == null) rear = null;
        return data;
    }

    @Override
    public T getFront() {
        if (isEmpty()) return null;
        return front.data;
    }

    @Override
    public boolean isEmpty() {
        return front == null;
    }

    @Override
    public void clear() {
        front = null;
        rear = null;
    }
    
public List<T> toList() {
    List<T> list = new ArrayList<>();
    Node<T> current = front;
    while (current != null) {
        list.add(current.data);
        current = current.next;
    }
    return list;
}
}

