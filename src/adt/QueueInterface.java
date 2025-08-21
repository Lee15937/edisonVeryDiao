/**
 *
 * @author Choi Kah Wai
 */

package adt;

import java.util.List;

public interface QueueInterface<T> {
    void enqueue(T newEntry);
    T dequeue();
    T getFront();
    boolean isEmpty();
    List<T> toList();
    void clear();
}