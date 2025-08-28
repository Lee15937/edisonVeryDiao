package adt;

/**
 *
 * @author kosoo
 */
import java.io.Serializable;
import java.util.Iterator;


public class DoubleLinkedList<T> implements LinkedListInterface<T>, Serializable {

    private Node firstNode;                     
    private Node lastNode;               
    private int num;


    @Override
    public void clear() {
        firstNode = lastNode = null;    
        num = 0;                        
    }

    /**
     * Adds a new element to the end of the linked list.
     *
     * @param newElement The element to be added.
     * @return True if the element is added successfully.
     */
    @Override
    public boolean add(T newElement) {
        if (newElement != null) {
            Node newNode = new Node(newElement);

            if (isEmpty()) {
                firstNode = newNode;       
                lastNode = newNode;
            } else {
                newNode.prev = lastNode;    
                lastNode.next = newNode;
                lastNode = newNode;
            }

            num++;
        }
        return false;
    }

    /**
     * Adds a new element at the specified index in the linked list.
     *
     * @param index The index at which the element should be added.
     * @param newElement The element to be added.
     * @return True if the element is added successfully.
     */
    @Override
    public boolean add(int index, T newElement) {
        if (newElement == null || !inAddRange(index)) {
            return false;
        } else {
            Node newNode = new Node(newElement);
            if (index == 0) {
                if (isEmpty()) {
                    add(newElement);
                    return true;        
                } else {
                    newNode.next = firstNode;
                    firstNode.prev = newNode;
                    firstNode = newNode;
                }
            } else if (index == num) {
                lastNode.next = newNode;
                newNode.prev = lastNode;
                lastNode = newNode;
            } else {
                Node nodeCurrent = travel(index);
                nodeCurrent.prev.next = newNode;
                newNode.prev = nodeCurrent.prev;
                newNode.next = nodeCurrent;
                nodeCurrent.prev = newNode;
            }
            num++;
            return true;
        }
    }

    /**
     * Adds all the provided elements to the end of the linked list.
     *
     * @param newElements The elements to be added.
     * @return True if all elements are added successfully.
     */
    @Override
    public boolean addAll(T... newElements) {
        if (newElements != null) {
            if (isElementsValid(newElements)) {
                for (T element : newElements) {
                    add(element);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the linked list contains the specified element.
     *
     * @param element The element to be checked.
     * @return True if the element is found in the linked list.
     */
    @Override
    public boolean contains(T element) {
        if (element != null) {
            return travel(element) != null;
        }
        return false;
    }

    @Override
    public T get(int index) {
        T data = null;
        if (inRange(index)) {
            Node nodeCurrent = travel(index);
            data = nodeCurrent.data;
        }
        return data;
    }

    /**
     * Retrieves the element at the specified index in the linked list.
     *
     * @param element The index of the element to retrieve.
     * @return The element at the specified index, or null if index is out of
     * bounds.
     */
    @Override
    public int indexOf(T element) {
        if (element != null) {
            int index = 0;
            for (Node nodeCurrent = firstNode; nodeCurrent != null
                    && inRange(index); index++, nodeCurrent = nodeCurrent.next) {
                if (nodeCurrent.data.equals(element)) {
                    return index;
                }
            }
        }
        return -1;
    }

    /**
     * Checks if the linked list is empty.
     *
     * @return True if the linked list is empty.
     */
    @Override
    public boolean isEmpty() {
        return num == 0;
    }

    /**
     * Removes the first occurrence of the specified element from the linked
     * list.
     *
     * @param element The element to be removed.
     * @return True if the element is removed successfully.
     */
    @Override
    public boolean remove(T element) {
        if (element == null || isEmpty()) {
            return false;
        } else {
            Node nodeCurrent = travel(element);
            if (nodeCurrent != null) {
                remove(nodeCurrent);
                return true;
            }
            return false;
        }
    }

    /**
     * Removes the element at the specified index from the linked list.
     *
     * @param index The index of the element to be removed.
     * @return True if the element is removed successfully.
     */
    @Override
    public boolean remove(int index) {
        if (isEmpty() || !inRange(index)) {
            return false;
        } else {
            remove(travel(index));
            return true;
        }
    }

    /**
     * Removes all occurrences of the specified elements from the linked list.
     *
     * @param elements The elements to be removed.
     * @return True if all specified elements are removed successfully.
     */
    @Override
    public boolean removeAll(T... elements) {
        if (isEmpty() || !isElementsValid(elements)) {
            return false;
        } else {
            for (T element : elements) {
                remove(element);
            }
            return true;
        }
    }

    /**
     * Replaces the element at the specified index with the new element.
     *
     * @param index The index of the element to be replaced.
     * @param newElement The new element to be placed at the specified index.
     * @return True if the replacement is successful.
     */
    @Override
    public boolean set(int index, T newElement) {
        if (isEmpty() || !inRange(index) || newElement == null) {
            return false;
        } else {
            Node nodeCurrent = travel(index);
            nodeCurrent.data = newElement;
            return true;
        }
    }

    /**
     * Returns the number of elements in the linked list.
     *
     * @return The number of elements in the linked list.
     */
    @Override
    public int sizeOf() {
        return num;
    }

    /**
     * Creates a new linked list containing elements that match the given
     * condition.
     *
     * @param list The condition to filter the elements.
     * @return A new linked list containing the filtered elements.
     */

    public LinkedListInterface where(WhereClause<T> list) {
        LinkedListInterface<T> linkedList = new DoubleLinkedList<>();

        for (Node nodeCurrent = firstNode; nodeCurrent != null; nodeCurrent = nodeCurrent.next) {
            if (list.match(nodeCurrent.data)) {
                linkedList.add(nodeCurrent.data);
            }
        }
        return linkedList;
    }

    /**
     * Orders the elements in the linked list according to the given condition.
     *
     * @param list The condition to order the elements.
     */
    public void orderBy(OrderClause<T> list) {
        int endIndex = num - 1;

        while (bubbleSort(endIndex--, list)) {
        }
    }

    /**
     * Returns the first element that matches the given condition.
     *
     * @param list The condition to search for the first element.
     * @return The first element that matches the condition, or null if not
     * found.
     */
    public T firstOrDefault(FirstOrDefaultClause<T> list) {
        T data = null;
        boolean found = false;
        for (Node nodeCurrent = firstNode; nodeCurrent != null && !found; nodeCurrent = nodeCurrent.next) {
            if (list.match(nodeCurrent.data)) {
                data = nodeCurrent.data;

                found = true;
            }
        }
        return data;
    }

    /**
     * Returns an iterator over the elements in the linked list.
     *
     * @return An iterator over the elements in the linked list.
     */
    @Override
    public Iterator<T> getIterator() {
        return new DoublyLinkListIterator();
    }

    /**
     * Represents a node in the doubly linked list.
     */
    private class Node implements Serializable {

        private T data;
        private Node next;
        private Node prev;

        /**
         * Constructs a new Node with the given data.
         *
         * @param data The data to be stored in the node.
         */
        private Node(T data) {
            this.data = data;
        }
    }

    /**
     * Sorts elements in the linked list using the bubble sort algorithm.
     *
     * @param endIndex The index up to which sorting is performed.
     * @param list The condition for sorting the elements.
     * @return True if any changes were made during the sorting process.
     */
    private boolean bubbleSort(int endIndex, OrderClause<T> list) {
        int beginIndex = 0;
        boolean hasChanges = false;
        for (Node nodeCurrent = firstNode; beginIndex < endIndex; beginIndex++, nodeCurrent = nodeCurrent.next) {
            if (list.compare(nodeCurrent.data, nodeCurrent.next.data) == OrderClause.MOVE_BACKWARD) {
                T temp = nodeCurrent.data;
                nodeCurrent.data = nodeCurrent.next.data;
                nodeCurrent.next.data = temp;
                hasChanges = true;
            }
        }
        return hasChanges;
    }

    /**
     * Removes the specified node from the linked list.
     *
     * @param nodeCurrent The node to be removed.
     */
    private void remove(Node nodeCurrent) {
        if (nodeCurrent == firstNode && nodeCurrent == lastNode) {
            firstNode = null;
            lastNode = null;
        } else if (nodeCurrent == firstNode) {
            firstNode.next.prev = null;
            firstNode = firstNode.next;
        } else if (nodeCurrent == lastNode) {
            lastNode.prev.next = null;
            lastNode = lastNode.prev;
        } else {
            nodeCurrent.prev.next = nodeCurrent.next;
            nodeCurrent.next.prev = nodeCurrent.prev;
        }
        num--;
    }

    /**
     * Checks if the specified elements are valid (not null).
     *
     * @param newElements The elements to be checked.
     * @return True if all elements are valid, false otherwise.
     */
    private boolean isElementsValid(T... newElements) {
        boolean valid = true;
        for (int i = 0; i < newElements.length && valid; i++) {
            if (newElements[i] == null) {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Traverses the linked list to find the node containing the specified
     * element.
     *
     * @param element The element to search for.
     * @return The node containing the element, or null if not found.
     */
    private Node travel(T element) {
        Node nodeCurrent = firstNode;
        boolean arrive = false;

        while (nodeCurrent != null && !arrive) {
            if (nodeCurrent.data.equals(element)) {
                arrive = true;
            } else {
                nodeCurrent = nodeCurrent.next;
            }
        }
        return nodeCurrent;
    }

    /**
     * Traverses the linked list to find the node at the specified destination
     * index.
     *
     * @param dest The index of the destination node.
     * @return The node at the specified index.
     */
    private Node travel(int dest) {
        int dev = num / 2;
        return dest < dev ? travelFromFirstTo(dest) : travelFromLastTo(dest);
    }

    /**
     * Traverses the linked list from the last node towards the specified
     * destination index.
     *
     * @param dest The index of the destination node.
     * @return The node at the specified index.
     */
    private Node travelFromLastTo(int dest) {
        Node nodeCurrent = lastNode;
        int begin = num - 1;

        while (begin != dest) {
            nodeCurrent = nodeCurrent.prev;
            begin--;
        }
        return nodeCurrent;
    }

    /**
     * Traverses the linked list from the first node towards the specified
     * destination index.
     *
     * @param dest The index of the destination node.
     * @return The node at the specified index.
     */
    private Node travelFromFirstTo(int dest) {
        Node nodeCurrent = firstNode;
        int begin = 0;

        while (begin != dest) {
            nodeCurrent = nodeCurrent.next;
            begin++;
        }
        return nodeCurrent;
    }

    /**
     * Checks if the specified index is within the valid range for adding an
     * element.
     *
     * @param index The index to be checked.
     * @return True if the index is within the valid range, false otherwise.
     */
    private boolean inAddRange(int index) {
        return index >= 0 && index <= num;
    }

    /**
     * Checks if the specified index is within the valid range of the linked
     * list.
     *
     * @param index The index to be checked.
     * @return True if the index is within the valid range, false otherwise.
     */
    private boolean inRange(int index) {
        return index >= 0 && index < num;
    }

    /**
     * Returns a string representation of the elements in the linked list.
     *
     * @return A string representation of the elements in the linked list.
     */
    @Override
    public String toString() {
        String str = "";
        for (Node nodeCurrent = firstNode; nodeCurrent != null; nodeCurrent = nodeCurrent.next) {
            str += nodeCurrent.data + "\n";
        }
        return str;
    }

    /**
     * Implements an iterator for iterating through the linked list.
     */
    private class DoublyLinkListIterator implements Iterator<T> {

        Node nodeCurrent = firstNode;

        /**
         * Checks if there is a next element in the linked list.
         *
         * @return True if there is a next element, false otherwise.
         */
        @Override
        public boolean hasNext() {
            return nodeCurrent != null;
        }

        /**
         * Retrieves the next element from the linked list.
         *
         * @return The next element.
         */
        @Override
        public T next() {
            T data = null;
            if (hasNext()) {
                data = nodeCurrent.data;
                nodeCurrent = nodeCurrent.next;
            }
            return data;
        }
    }
}
