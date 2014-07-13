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
 * Class Cons, a component of the List<T> composite hierarchy.
 * Note: null is not allowed as a value for any field.
 */
public class Cons<T> extends PureList<T> {
    private final T _first;
    private final PureList<T> _rest;
    private int _hashCode;
    private boolean _hasHashCode = false;

    /**
     * Constructs a Cons.
     *
     * @throws java.lang.IllegalArgumentException
     *          if any parameter to the constructor is null.
     */
    public Cons(T in_first, PureList<T> in_rest) {
        super();

        if (in_first == null) {
            throw new java.lang.IllegalArgumentException(
                    "Parameter 'first' to the Cons constructor was null. This class may not have null field values.");
        }
        _first = in_first;

        if (in_rest == null) {
            throw new java.lang.IllegalArgumentException(
                    "Parameter 'rest' to the Cons constructor was null. This class may not have null field values.");
        }
        _rest = in_rest;
    }

    final public T getFirst() {
        return _first;
    }

    final public PureList<T> getRest() {
        return _rest;
    }

    public boolean isEmpty() {
        return false;
    }

    public int size() {
        return 1 + getRest().size();
    }

    public <U> PureList<U> map(Fn<T, U> fn) {
        return getRest().map(fn).cons(fn.apply(getFirst()));
    }

    public boolean contains(T candidate) {
        return getFirst().equals(candidate) || getRest().contains(candidate);
    }

    public PureList<T> append(PureList<T> that) {
        return getRest().append(that).cons(getFirst());
    }

    public PureList<T> reverse() {
        return reverse(new Empty<T>());
    }

    public PureList<T> reverse(PureList<T> result) {
        return getRest().reverse(result.cons(getFirst()));
    }


    /**
     * Implementation of equals that is based on the values
     * of the fields of the object. Thus, two objects
     * created with identical parameters will be equal.
     */
    @Override
    @SuppressWarnings ("unchecked")
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if ((obj.getClass() != this.getClass()) || (obj.hashCode() != this.hashCode())) {
            return false;
        } else {
            Cons casted = (Cons) obj;
            if (!(getFirst().equals(casted.getFirst()))) return false;
            if (!(getRest().equals(casted.getRest()))) return false;
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
        code ^= getFirst().hashCode();
        code ^= getRest().hashCode();
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
            private PureList<T> current = Cons.this;

            public boolean hasNext() {
                return !current.isEmpty();
            }

            public T next() {
                if (current.isEmpty()) {
                    throw new NoSuchElementException("Attempt to take next at end of iterator");
                } else {
                    Cons<T> _current = (Cons<T>) current;
                    current = _current.getRest();
                    return _current.getFirst();
                }
            }

            public void remove() {
                throw new UnsupportedOperationException("Attempt to remove from iterator on a PureList");
            }
        };
    }
}
