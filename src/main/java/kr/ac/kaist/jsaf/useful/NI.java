/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

/**
 * A bunch of generic methods, inspired by the knights who say this.getClass().getSimpleName().
 * <p/>
 * The vowels all throw exceptions.
 * <p/>
 * The consonants are filters.
 */
public class NI {

    /**
     * Not Allowed.
     */
    public static <T> T na() {
        throw new Error("Not allowed");
    }

    public static <T> T na(String what) {
        throw new Error("Not allowed: " + what);
    }

    /**
     * Not Implemented.
     */
    public static <T> T ni() {
        throw new Error("Not implemented");
    }

    /**
     * Not Possible.
     */
    public static <T> T np() {
        throw new Error("Not possible");
    }

    /**
     * Not Yet.
     */
    public static <T> T nyi() {
        throw new Error("Not yet implemented");
    }

    /**
     * Not Yet.
     */
    public static <T> T nyi(String name) {
        throw new Error(name + ": Not yet implemented");
    }

    /**
     * Identity function for non-nulls.
     * Throws a CHECKED exception.
     */
    public static <T> T cnnf(T x) throws CheckedNullPointerException {
        if (x == null) throw new CheckedNullPointerException("Null not allowed");
        return x;
    }

    /**
     * Identity function for non-nulls.
     */
    public static <T> T nnf(T x) {
        if (x == null) throw new NullPointerException("Null not allowed");
        return x;
    }
}
