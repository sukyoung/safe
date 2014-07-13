/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import edu.rice.cs.plt.lambda.Lambda2;

import java.util.ArrayList;
import java.util.List;

public abstract class Fn2<T, U, V> implements Lambda2<T, U, V> {
    public abstract V apply(T t, U u);

    public V value(T t, U u) {
        return apply(t, u);
    }

    public final static Fn2<String, String, String> stringAppender = new Fn2<String, String, String>() {
        @Override
        public String apply(String t, String u) {
            return t + u;
        }
    };

    public final static <W> Fn2<List<W>, List<W>, List<W>> listAppender() {
        return new Fn2<List<W>, List<W>, List<W>>() {
            @Override
            public List<W> apply(List<W> t, List<W> u) {
                ArrayList<W> a = new ArrayList<W>(t);
                a.addAll(u);
                return a;
            }
        };
    }

}
