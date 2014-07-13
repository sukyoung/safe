/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import java.util.Iterator;

/**
 * This class is an implementation of purely functional lists,
 * unlike those in java.util, which are mutable lists and are
 * therefore unsuitable for certain programming tasks.
 */
public abstract class PureList<T> implements Iterable<T> {
    public static <T> PureList<T> make(T... elts) {
        PureList<T> result = new Empty<T>();
        for (int i = elts.length - 1; i >= 0; i--) {
            result = result.cons(elts[i]);
        }
        return result;
    }

    public static <T> PureList<T> make(T e1, T e2) {
        PureList<T> result = new Empty<T>();
        result = result.cons(e2);
        result = result.cons(e1);
        return result;
    }

    public static <T> PureList<T> make(T e1) {
        PureList<T> result = new Empty<T>();
        result = result.cons(e1);
        return result;
    }

    public static <T> PureList<T> make() {
        PureList<T> result = new Empty<T>();
        return result;
    }

    public static <T> PureList<T> fromJavaList(java.util.List<T> elts) {
        PureList<T> result = new Empty<T>();
        for (T elt : elts) {
            result = result.cons(elt);
        }
        return result.reverse();
    }

    public java.util.List<T> toJavaList() {
        java.util.List<T> result = new java.util.LinkedList<T>();
        PureList<T> remainder = this;

        while (!remainder.isEmpty()) {
            Cons<T> _remainder = (Cons<T>) remainder;
            result.add(_remainder.getFirst());
            remainder = _remainder.getRest();
        }
        return result;
    }

    public Object[] toArray() {
        return toArray(size());
    }

    /* @pre this.size() >= n */
    public Object[] toArray(int n) {
        Object[] result = new Object[n];

        PureList<T> remainder = this;
        for (int i = 0; i < n; i++) {
            Cons<T> _remainder = (Cons<T>) remainder;
            result[i] = _remainder.getFirst();
            remainder = _remainder.getRest();
        }
        return result;
    }

    public abstract boolean isEmpty();

    public abstract int size();

    public abstract <U> PureList<U> map(Fn<T, U> fn);

    public abstract boolean contains(T candidate);

    public final PureList<T> cons(T e1, T e2, T e3, T... elts) {
        PureList<T> result = this;
        for (int i = elts.length - 1; i >= 0; i--) {
            result = new Cons<T>(elts[i], result);
        }
        result = new Cons<T>(e3, result);
        result = new Cons<T>(e2, result);
        result = new Cons<T>(e1, result);
        return result;
    }

    public final PureList<T> cons(T e1, T e2) {
        PureList<T> result = this;
        result = new Cons<T>(e2, result);
        result = new Cons<T>(e1, result);
        return result;
    }

    public PureList<T> cons(T e1) {
        PureList<T> result = this;
        result = new Cons<T>(e1, result);
        return result;
    }

    public final PureList<T> cons() {
        PureList<T> result = this;
        return result;
    }

    public abstract PureList<T> append(PureList<T> that);

    public PureList<T> reverse() {
        return reverse(new Empty<T>());
    }

    public abstract PureList<T> reverse(PureList<T> result);

    public abstract Iterator<T> iterator();
}
