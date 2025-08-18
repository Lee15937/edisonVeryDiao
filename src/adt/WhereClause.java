
package adt;

/**
 *
 * @author kosoo
 */
public interface WhereClause<T> {

    /**
     * Task: Match 1 or more element(s)
     *
     * @param element
     * @return
     */
    boolean match(T element);
}
