/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

public class Triple<T, U, V> {
    private T a;
    private U b;
    private V c;

    public Triple(T a, U b, V c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <T, U, V> Triple<T, U, V> make(T a, U b, V c) {
        return new Triple<T, U, V>(a, b, c);
    }

    final public T getA() {
        return a;
    }

    final public U getB() {
        return b;
    }

    final public V getC() {
        return c;
    }
    
    final public T first() {
        return a;
    }

    final public U second() {
        return b;
    }

    final public V third() {
        return c;
    }
    
    final public Triple<T, U, V> setA(T t) {
        return new Triple(t, b, c);
    }
    
    final public Triple<T, U, V> setB(U u) {
        return new Triple(a, u, c);
    }
    
    final public Triple<T, U, V> setC(V v) {
        return new Triple(a, b, v);
    }
    
    public String toString() {
        return "(" + a + "," + b + "," + c + ")";
    }

    final public boolean equals(Object o) {
        if (o instanceof Triple) {
            Triple t = (Triple) o;
            return t.a.equals(a) && t.b.equals(b) && t.c.equals(c);
        }
        return false;
    }

    final public int hashCode() {
        return (MagicNumbers.Z + a.hashCode()) * (MagicNumbers.Y + b.hashCode()) * (MagicNumbers.X + c.hashCode());
    }
}
