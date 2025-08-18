
package adt;

/**
 *
 * @author kosoo
 */
import java.util.Iterator;


public interface LinkedListInterface<T> {

    /**
     * Task: clear all
     */
    void clear();

    /**
     * Task: Add a new entry to last of the list
     *
     * @param newElement
     * @return True if the element is added successfully.
     */
    boolean add(T newElement);

    /**
     * Task: Add a new entry to any place of the list
     *
     * @param index
     * @param newElement
     * @return True if the element is added successfully.
     */
    boolean add(int index, T newElement);

    /**
     * Task: Add all the new entries to the list
     *
     * @param newElements
     * @return True if all elements are added successfully.
     */
    boolean addAll(T... newElements);

    /**
     * Task: Get the record contains the specified character or string
     *
     * @param element
     * @return records
     */
    boolean contains(T element);

    /**
     * Task: Get the specified record in the list
     *
     * @param index
     * @return the specified record
     */
    T get(int index);

    /**
     * Task: Get the record's index in the list
     *
     * @param element
     * @return number
     */
    int indexOf(T element);

    /**
     * Task: Check the list whether is empty
     *
     * @return true or false
     */
    boolean isEmpty();

    /**
     * Task: Remove the specified record from the list
     *
     * @param element
     * @return True if the element is removed successfully.
     */
    boolean remove(T element);

    /**
     * Task: Remove the record from the list by index
     *
     * @param index
     * @return True if the element is removed successfully.
     */
    boolean remove(int index);

    /**
     * Task: Remove all elements related to the record from the list
     *
     * @param elements
     * @return
     */
    boolean removeAll(T... elements);

    /**
     * Task: Set new record with index to the list
     *
     * @param index
     * @param newElement
     * @return
     */
    boolean set(int index, T newElement);

    /**
     * Task: Get number of records in the list
     *
     * @return
     */
    int sizeOf();
    
    /**
     * Task: Get Iterator
     *
     * @return
     */
    Iterator<T> getIterator();
}
