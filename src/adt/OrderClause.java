
package adt;

/**
 *
 * @author kosoo
 */
public interface OrderClause<T> {

    /** A constant representing moving a record to the front. */

    public static final int MOVE_FORWARD = -1;
    /** A constant representing moving a record to the back. */

    public static final int MOVE_BACKWARD = 1;

    /**
     * Compares two records.
     *
     * @param t1 The first record to compare.
     * @param t2 The second record to compare.
     * @return A value indicating the comparison result. Returns
     * {@link #MOVE_FORWARD} if t1 should move to the front,
     * {@link #MOVE_BACKWARD} if t1 should move to the back, or other values
     * based on specific comparison logic.
     */
    int compare(T t1, T t2);
}
