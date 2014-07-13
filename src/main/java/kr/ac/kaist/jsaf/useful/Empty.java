/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Class Empty, a component of the List<T> composite hierarchy.
 * Note: null is not allowed as a value for any field.
 */
public class Empty<T> extends PureList<T> {
    private int _hashCode;
    private boolean _hasHashCode = false;

    /**
     * Constructs a Empty.
     *
     * @throws java.lang.IllegalArgumentException
     *          if any parameter to the constructor is null.
     */
    public Empty() {
        super();
    }

    public boolean isEmpty() {
        return true;
    }

    public int size() {
        return 0;
    }

    public <U> PureList<U> map(Fn<T, U> fn) {
        return new Empty<U>();
    }

    public boolean contains(T candidate) {
        return false;
    }

    public PureList<T> cons(T elt) {
        return new Cons<T>(elt, this);
    }

    public PureList<T> append(PureList<T> that) {
        return that;
    }

    public PureList<T> reverse(PureList<T> result) {
        return result;
    }


    /**
     * Implementation of equals that is based on the values
     * of the fields of the object. Thus, two objects
     * created with identical parameters will be equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if ((obj.getClass() != this.getClass()) || (obj.hashCode() != this.hashCode())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Implementation of hashCode that is consistent with
     * equals. The value of the hashCode is formed by
     * XORing the hashcode of the class object with
     * the hashcodes of all the fields of the object.
     */
    protected int generateHashCode() {
        int code = getClass().hashCode();
        return code;
    }

    public final int hashCode() {
        if (!_hasHashCode) {
            _hashCode = generateHashCode();
            _hasHashCode = true;
        }
        return _hashCode;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            public boolean hasNext() {
                return false;
            }

            public T next() {
                throw new NoSuchElementException("Attempt to call next on iterator of an Empty PureList");
            }

            public void remove() {
                throw new UnsupportedOperationException("Attempt to remove from an Empty PureList");
            }
        };
    }
}
