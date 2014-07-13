/*******************************************************************************
    Copyright 2008,2010, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Useful {
    static final String localMilliIso8601Format = "yyyy-MM-dd'T'HH:mm:ss.SSSzzz";

    static public final DateFormat localMilliDateFormat = new SimpleDateFormat(localMilliIso8601Format);

    public static String localNow(java.util.Date d) {
        return localMilliDateFormat.format(d);
    }

    public static String timeStamp() {
        return localNow(new java.util.Date());
    }

    public static <K, V> TreeMap<K, V> treeMap(Comparator<K> comp, Map<? extends K, ? extends V> map) {
        TreeMap<K, V> result = new TreeMap<K, V>(comp);
        result.putAll(map);
        return result;
    }

    /**
     * Returns a string containing String.valueOf each element of l,
     * separated by commas, all surrounded by parentheses.
     *
     * @param <T>
     * @param l
     * @return
     */
    public static <T> String listInParens(Collection<T> l) {
        return listInDelimiters("(", l, ")");
    }

    /**
     * Returns a string containing String.valueOf each element of l,
     * separated by commas, all surrounded by curly braces.
     *
     * @param <T>
     * @param l
     * @return
     */
    public static <T> String listInCurlies(Collection<T> l) {
        return listInDelimiters("{", l, "}");
    }

    public static <T> String listInCurlies(Object[] l) {
        return listInDelimiters("{", l, "}");
    }

    /**
     * Returns a string containing String.valueOf each element of l,
     * separated by commas, all surrounded by left and right delimiters.
     *
     * @param <T>
     * @param l
     * @return
     */
    public static <T> String listInDelimiters(String left, Collection<T> l, String right) {
        return listInDelimiters(left, l, right, ",");
    }
    
    public static <T> String listInDelimiters(String left, Object[] l, String right) {
        return listInDelimiters(left, l, right, ",");
    }

    /**
     * Returns a string containing String.valueOf each element of l,
     * separated by sep, all surrounded by left and right delimiters.
     *
     * @param <T>
     * @param l
     * @return
     */
    public static <T> String listInDelimiters(String left, Iterable<T> l, String right, String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append(left);
        boolean first = true;
        for (T x : l) {
            if (first) first = false;
            else sb.append(sep);
            sb.append(String.valueOf(x));
        }
        sb.append(right);
        return sb.toString();
    }

    public static <T> String listInDelimiters(String left, Object[] l, String right, String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append(left);
        boolean first = true;
        if (l != null) 
            for (Object x : l) {
            if (first) first = false;
            else sb.append(sep);
            sb.append(String.valueOf(x));
            }
        sb.append(right);
        return sb.toString();
    }

    public static <T> String listTranslatedInDelimiters(String left, Object[] l, String right, String sep, F<Object, String> foreach) {
        StringBuilder sb = new StringBuilder();
        sb.append(left);
        boolean first = true;
        if (l != null) 
            for (Object x : l) {
            if (first) first = false;
            else sb.append(sep);
            sb.append(foreach.apply(x));
            }
        sb.append(right);
        return sb.toString();
    }

    public static <T> String listTranslatedInDelimiters(String left, Iterable<T> l, String right, String sep, F<Object, String> foreach) {
        StringBuilder sb = new StringBuilder();
        sb.append(left);
        boolean first = true;
        if (l != null) 
            for (Object x : l) {
            if (first) first = false;
            else sb.append(sep);
            sb.append(foreach.apply(x));
            }
        sb.append(right);
        return sb.toString();
    }

    public static String coordInDelimiters(String left, int[] l, int hi, String right) {
        return coordInDelimiters(left, l, 0, hi, right);
    }

    public static String coordInDelimiters(String left, int[] l, String right) {
        return coordInDelimiters(left, l, 0, l.length, right);
    }

    public static String coordInDelimiters(String left, int[] l, int lo, int hi, String right) {
        StringBuilder sb = new StringBuilder();
        sb.append(left);
        boolean first = true;
        for (int i = lo; i < hi; i++) {
            if (first) first = false;
            else sb.append(",");
            sb.append(String.valueOf(l[i]));
        }
        sb.append(right);
        return sb.toString();
    }

    /**
     * As if listInParens(l1 CONCAT l2), except that the concatenation
     * is avoided.
     *
     * @param <T>
     * @param <U>
     * @param l1
     * @param l2
     * @return
     */
    public static <T, U> String listsInParens(List<T> l1, List<U> l2) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean first = true;
        for (T x : l1) {
            if (first) first = false;
            else sb.append(",");
            sb.append(String.valueOf(x));
        }
        for (U x : l2) {
            if (first) first = false;
            else sb.append(",");
            sb.append(String.valueOf(x));
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Returns a string containing String.valueOf each element of l,
     * separated by ".".
     * <p/>
     * dottedList(["cat", "dog"]) = "cat.dog"
     * dottedList(["cat"]) = "cat"
     * dottedList([]) = ""
     *
     * @param <T>
     * @param l
     * @return
     */
    public static <T> String dottedList(List<T> l) {
        if (l.size() == 1) /* Itty-bitty performance hack */ return String.valueOf(l.get(0));
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (T x : l) {
            if (first) first = false;
            else sb.append(".");
            sb.append(String.valueOf(x));
        }
        return sb.toString();
    }

    public static String backtrace(int start, int count) {
        StackTraceElement[] trace = (new Throwable()).getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start + count && i < trace.length; i++) {
            if (i > start) sb.append("\n");
            sb.append(String.valueOf(trace[i]));
        }
        return sb.toString();
    }

    public static <T> String listInOxfords(List<T> l) {
        return listInDelimiters("[\\", l, "\\]");
    }

    public static String inOxfords(String string1, String string2, String string3) {

        return "[\\" + string1 + ", " + string2 + ", " + string3 + "\\]";
    }

    public static String inOxfords(String string1, String string2) {

        return "[\\" + string1 + ", " + string2 + "\\]";
    }

    public static String inOxfords(String string1) {

        return "[\\" + string1 + "\\]";
    }

    /**
     * Returns the set of lists
     * <p/>
     * { e = l.add(i) | l IN s1, i IN s2 }
     *
     * @param <T>
     * @param s1
     * @param s2
     * @return
     */
    public static <T> Set<List<T>> setProduct(Set<List<T>> s1, Set<T> s2) {
        HashSet<List<T>> result = new HashSet<List<T>>();

        for (List<T> list : s1) {
            for (T elt : s2) {
                ArrayList<T> java_is_not_a_functional_language = new ArrayList<T>(list);
                java_is_not_a_functional_language.add(elt);
                result.add(java_is_not_a_functional_language);
            }
        }
        return result;
    }

    /**
     * Returns the set of lists
     * <p/>
     * { e = product.apply(i,j) | i IN s1, j IN s2 }
     *
     * @param <T>
     * @param s1
     * @param s2
     * @return
     */
    public static <T, U, V> Set<V> setProduct(Set<T> t, Set<U> u, Fn2<T, U, V> product) {
        HashSet<V> result = new HashSet<V>();
        for (T i : t) {
            for (U j : u) {
                result.add(product.apply(i, j));
            }
        }
        return result;
    }

    /**
     * Returns the set {e = verb.apply(i) | i IN s}
     *
     * @param <T>
     * @param <U>
     * @param s
     * @param verb
     * @return
     */

    public static <T, U> Set<U> applyToAll(Set<T> s, F<T, U> verb) {
        HashSet<U> result = new HashSet<U>();
        for (T i : s) {
            result.add(verb.apply(i));
        }
        return result;

    }

    public static <T> Set<T> matchingSubset(Iterable<T> s, F<T, Boolean> verb) {
        HashSet<T> result = new HashSet<T>();
        for (T i : s) {
            if (verb.apply(i)) result.add((i));
        }

        return result;

    }

    /**
     * Returns the LIST [ e = verb.apply(i) | i IN s ]
     *
     * @param <T>
     * @param <U>
     * @param s
     * @param verb
     * @return
     */

    public static <T, U> List<U> applyToAll(List<T> s, F<T, U> verb) {
        ArrayList<U> result = new ArrayList<U>();
        for (T i : s) {
            result.add(verb.apply(i));
        }
        return result;

    }
    
    public static <T> Boolean andReduction(Collection<T> s, F<T, Boolean> verb) {
        for (T i : s) {
            if (!verb.apply(i).booleanValue())
                return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static <T> Boolean orReduction(Collection<T> s, F<T, Boolean> verb) {
        for (T i : s) {
            if (verb.apply(i).booleanValue())
                return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    

    
    public static <T> List<T> applyToAllPossiblyReusing(List<T> s, F<T, T> verb) {
        int j = 0;
        for (T i : s) {
            T new_i = verb.apply(i);
            
            if (new_i != i) {
                /*
                 *  this will abandon the outer loop,
                 *  and execute one that allocates new
                 */
                int k = 0;
                ArrayList<T> result = new ArrayList<T>(s.size());

                for (T ii : s) {
                    result.add (k < j ? ii :     // copy
                                k == j ? new_i : // reuse
                                verb.apply(ii)); // compute
                    k++;
                }
                
                return result;
            }
            j++;
        }
        return s;

    }


    /**
     * Returns the LIST result APPEND [ e = verb.apply(i) | i IN s ]
     *
     * @param <T>
     * @param <U>
     * @param s
     * @param verb
     * @return
     */
    public static <T, U> List<U> applyToAllAppending(Collection<T> s, F<T, U> verb, List<U> result) {
        for (T i : s) {
            result.add(verb.apply(i));
        }
        return result;

    }

    /**
     * Returns the SET result UNION { e = verb.apply(i) | i IN s }
     *
     * @param <T>
     * @param <U>
     * @param s
     * @param verb
     * @return
     */
    public static <T, U> Set<U> applyToAllInserting(Collection<T> s, F<T, U> verb, Set<U> result) {
        for (T i : s) {
            result.add(verb.apply(i));
        }
        return result;
    }

    public static <T, U> Set<U> applyToAllInserting(List<T> s, F<T, U> verb, Set<U> result) {
        for (T i : s) {
            result.add(verb.apply(i));
        }
        return result;
    }

    public static <T> Set<T> set(Iterable<T> xs) {
        HashSet<T> result = new HashSet<T>();

        for (T x : xs) {
            if (x != null) result.add(x);
        }
        return result;
    }


    public static <T> Set<T> set() {
        return Collections.emptySet();
    }

    public static <T> Set<T> set(T x1) {
        HashSet<T> result = new HashSet<T>();
        result.add(x1);
        return result;
    }

    public static <T> Set<T> set(T x1, T x2) {
        HashSet<T> result = new HashSet<T>();
        result.add(x1);
        result.add(x2);
        return result;
    }

    public static <T> Set<T> set(T x1, T x2, T x3) {
        HashSet<T> result = new HashSet<T>();
        result.add(x1);
        result.add(x2);
        result.add(x3);
        return result;
    }


    /* Union, treating null as empty. */
    public static <T> Set<T> union(Iterable<? extends Collection<T>> xs) {
        HashSet<T> result = new HashSet<T>();
        for (Collection<T> x : xs) {
            if (x != null) result.addAll(x);
        }
        return result;
    }

    public static <T> Set<T> union(Collection<T> x1, Collection<T> x2, Collection<T> x3, Collection<T> x4) {
        HashSet<T> result = new HashSet<T>();
        result.addAll(x1);
        result.addAll(x2);
        result.addAll(x3);
        result.addAll(x4);
        return result;
    }

    public static <T> Set<T> union(Collection<T> x1,
                                   Collection<T> x2,
                                   Collection<T> x3,
                                   Collection<T> x4,
                                   Collection<T> x5,
                                   Collection<T> x6) {
        HashSet<T> result = new HashSet<T>();
        result.addAll(x1);
        result.addAll(x2);
        result.addAll(x3);
        result.addAll(x4);
        result.addAll(x5);
        result.addAll(x6);
        return result;
    }

    public static <T> Set<T> union(Collection<T> x1, Collection<T> x2, Collection<T> x3) {
        HashSet<T> result = new HashSet<T>();
        result.addAll(x1);
        result.addAll(x2);
        result.addAll(x3);
        return result;
    }

    public static <T> Set<T> union(Collection<T> x1, Collection<T> x2) {
        HashSet<T> result = new HashSet<T>();
        result.addAll(x1);
        result.addAll(x2);
        return result;
    }

    public static <T> Set<T> difference(Collection<T> x1, Collection<T> x2) {
        HashSet<T> result = new HashSet<T>();
        result.addAll(x1);
        result.removeAll(x2);
        return result;
    }

    // Don't support singleton or empty unions -- singletons lead to type
    // signature clashes with the varargs case.

    public static <T> T clampedGet(List<T> l, int j) {
        int s = l.size();
        return j < s ? l.get(j) : l.get(s - 1);
    }

    public static <U> List<U> list(List<? extends U> all) {
        List<U> l = new ArrayList<U>();
        l.addAll(all);
        return l;
    }

    public static <U> List<U> list(U[] all) {
        List<U> l = new ArrayList<U>(all.length);
        for (U x : all) {
            l.add(x);
        }
        return l;
    }

    public static <T> List<T> list(Iterable<? extends T> xs) {
        ArrayList<T> result;
        if (xs instanceof Collection<?>) {
            result = new ArrayList<T>(((Collection<?>) xs).size());
        } else {
            result = new ArrayList<T>();
        }
        for (T x : xs) {
            result.add(x);
        }
        return result;
    }

    /**
     * Returns the LIST [ e = verb.apply(i) != null | i IN s ]
     *
     * @param <T>
     * @param <U>
     * @param s
     * @param verb
     * @return
     */

    public static <T, U> List<U> filteredList(Iterable<? extends T> l, F<T, U> f) {
        ArrayList<U> result = new ArrayList<U>();
        for (T t : l) {
            U u = f.apply(t);
            if (u != null) result.add(u);
        }
        return result;
    }

    /**
     * Returns the LIST [ e = verb.apply(i) != false | i IN s ]
     *
     * @param <T>
     * @param <U>
     * @param s
     * @param verb
     * @return
     */

    public static <T> List<T> filter(final Iterable<? extends T> l, final F<T, Boolean> f) {
        return filteredList(l, new F<T, T>() {
            public T apply(T t) {
                if (f.apply(t)) {
                    return t;
                }
                return null;
            }
        });
        /* Raw implementation
        List<T> result = new List<T>();
        for ( T t : l){
            if ( f.apply(t) ){
                result.add(t);
            }
        }
        return result;
        */
    }

    public static <T, U> List<T> convertList(Iterable<? extends U> list) {
        return filteredList(list, new F<U, T>() {
            public T apply(U u) {
                return (T) u;
            }
        });
    }

    /**
     * Returns the ORDERED (by c) SET { e = f.apply(i) != null | i IN l }
     *
     * @param <T>
     * @param <U>
     * @param s
     * @param verb
     * @return
     */

    public static <T, U> SortedSet<U> filteredSortedSet(Iterable<? extends T> l, F<T, U> f, Comparator<U> c) {
        SortedSet<U> result = new TreeSet<U>(c);
        for (T t : l) {
            U u = f.apply(t);
            if (u != null) result.add(u);
        }
        return result;
    }


    public static <T> List<T> list(T x1, T x2, T x3, T x4) {
        ArrayList<T> result = new ArrayList<T>(4);
        result.add(x1);
        result.add(x2);
        result.add(x3);
        result.add(x4);
        return result;
    }

    public static <T> List<T> list(T x1, T x2, T x3) {
        ArrayList<T> result = new ArrayList<T>(3);
        result.add(x1);
        result.add(x2);
        result.add(x3);
        return result;
    }

    public static <T> List<T> list(T x1, T x2) {
        ArrayList<T> result = new ArrayList<T>(2);
        result.add(x1);
        result.add(x2);
        return result;
    }

    public static <T,U> Map<T,U> map(List<T> x1, List<U> x2) {
        HashMap<T,U> result = new HashMap<T,U>();
        return map(x1, x2, result);
    }

    public static <T,U> Map<T,U> map(List<T> x1, List<U> x2, Map<T,U> result) {
        int l = x1.size();
        for (int i = 0; i < l; i++)
            result.put(x1.get(i), x2.get(i));
        return result;
    }

    public static <T> List<T> list(T x1) {
        ArrayList<T> result = new ArrayList<T>(1);
        result.add(x1);
        return result;
    }

    public static <T> List<T> list() {
        return Collections.emptyList();
    }

    public static <U, T extends U> List<U> list(List<T> rest, U last) {
        List<U> l = new ArrayList<U>();
        l.addAll(rest);
        l.add(last);
        return l;
    }

    public static <U, T extends U> List<U> list(U first, List<T> rest) {
        List<U> l = new ArrayList<U>();
        l.add(first);
        l.addAll(rest);
        return l;
    }

    public static <T> List<T> immutableTrimmedList(List<T> x) {
        int l = x.size();
        if (l == 0) return Collections.<T>emptyList();
        if (l == 1) return Collections.<T>singletonList(x.get(0));
        return new ArrayList<T>(x);

    }

    public static <T> List<T> immutableTrimmedList(PureList<T> x) {
        int l = x.size();
        if (l == 0) return Collections.<T>emptyList();
        if (l == 1) return Collections.<T>singletonList(x.iterator().next());
        ArrayList<T> a = new ArrayList<T>(l);
        for (T y : x) {
            a.add(y);
        }
        return a;

    }

    /**
     * Calls {@ prepend}
     */
    public static <T> List<T> cons(T x, List<T> y) {
        return prepend(x, y);
    }

    public static <T> List<T> prepend(T x, List<T> y) {
        ArrayList<T> result = new ArrayList<T>(1 + y.size());
        result.add(x);
        result.addAll(y);
        return result;
    }


    public static <T> List<T> removeIndex(int i, List<T> y) {
        int l = y.size();
        if (i == 0) return y.subList(1, l);
        if (i == l - 1) return y.subList(0, l - 1);
        ArrayList<T> result = new ArrayList<T>(y.size() - 1);
        result.addAll(y.subList(0, i));
        result.addAll(y.subList(i + 1, l));
        return result;
    }

    public static <T, U> List<U> prependMapped(T x, List<T> y, F<T, U> f) {
        ArrayList<U> result = new ArrayList<U>(1 + y.size());
        result.add(f.apply(x));
        for (T t : y) {
            result.add(f.apply(t));
        }
        return result;
    }

    public static <T> List<T> concat(Iterable<Collection<T>> xs) {
        ArrayList<T> result = new ArrayList<T>();

        for (Collection<T> x : xs) {
            result.addAll(x);
        }
        result.trimToSize();
        return result;
    }

    /**
     * This method can be used to get List<? extends T> from List<T>. This can be used for
     * (somewhat naughty) purposes like casting Lists to lists that contain subtypes.
     */
    public static <T> List<? extends T> questionMarkList(List<? extends T> list) {
        return list;
    }

    public static <T> List<T> concat(Collection<? extends T> x1, Collection<? extends T> x2) {
        ArrayList<T> result = new ArrayList<T>();
        result.addAll(x1);
        result.addAll(x2);
        result.trimToSize();
        return result;
    }

    public static <T> List<T> concat(Collection<T> x1) {
        ArrayList<T> result = new ArrayList<T>(x1);
        return result;
    }

    public static <T> List<T> concat() {
        ArrayList<T> result = new ArrayList<T>();
        return result;
    }

    public static <T> T singleValue(Set<T> s) {
        int si = s.size();
        if (si == 0) throw new Error("Empty set where singleton expected");
        if (si > 1) throw new Error("Multiple-element set where singleton expected");
        for (T e : s) {
            return e;
        }

        return NI.<T>np();
    }

    public static <T extends Comparable<T>, U extends Comparable<U>> int compare(T a, T b, U c, U d) {
        int x = a.compareTo(b);
        if (x != 0) return x;
        return c.compareTo(d);
    }

    public static <T extends Comparable<T>, U extends Comparable<U>, V extends Comparable<V>> int compare(T a,
                                                                                                          T b,
                                                                                                          U c,
                                                                                                          U d,
                                                                                                          V e,
                                                                                                          V f) {
        int x = a.compareTo(b);
        if (x != 0) return x;
        x = c.compareTo(d);
        if (x != 0) return x;
        return e.compareTo(f);
    }

    public static <T extends Comparable<T>, U extends Comparable<U>, V extends Comparable<V>, W extends Comparable<W>> int compare(
            T a,
            T b,
            U c,
            U d,
            V e,
            V f,
            W g,
            W h) {
        int x = a.compareTo(b);
        if (x != 0) return x;
        x = c.compareTo(d);
        if (x != 0) return x;
        x = e.compareTo(f);
        if (x != 0) return x;
        return g.compareTo(h);
    }

    public static <T> int compare(T a, T b, Comparator<T> c1, Comparator<T> c2) {
        int x = c1.compare(a, b);
        if (x != 0) return x;
        return c2.compare(a, b);
    }

    public static <T> int compare(T a, T b, Comparator<T> c1, Comparator<T> c2, Comparator<T> c3) {
        int x = c1.compare(a, b);
        if (x != 0) return x;
        x = c2.compare(a, b);
        if (x != 0) return x;
        return c3.compare(a, b);
    }

    public static int countMatches(String input, String to_match) {
        int j = 0;
        int l = to_match.length();
        if (l == 0) return input.length() == 0 ? 1 : 0;

        int i = input.indexOf(to_match);

        while (i != -1) {
            j++;
            i = input.indexOf(to_match, i + l);
        }
        return j;
    }

    public static String extractAfterMatch(String input, String to_match) throws NotFound {
        int i = input.indexOf(to_match);
        if (i == -1) throw new NotFound();
        return input.substring(i + to_match.length());
    }

    public static String extractBeforeMatchOrAll(String input, String to_match) {
        int i = input.indexOf(to_match);
        if (i == -1) return input;
        return input.substring(0, i);
    }

    public static String extractBeforeMatch(String input, String to_match) throws NotFound {
        int i = input.indexOf(to_match);
        if (i == -1) throw new NotFound();
        return input.substring(0, i);
    }

    public static String extractBetweenMatch(String input, String before, String after) {
        int b = input.indexOf(before);
        if (b == -1) return null;
        input = input.substring(b + before.length());
        if (after == null) return input;
        int a = input.indexOf(after);
        if (a == -1) return null;
        return input.substring(0, a);
    }

    /**
     * Replaces all  occurrences of search in original with replace.
     *
     * @param original
     * @param search
     * @param replace
     * @return
     */
    public static String replace(String original, String search, String replace) {
        int searchLength = search.length();
        // One way of dealing with the empty string
        if (searchLength == 0) throw new IllegalArgumentException("Cannot replace empty string");
        // arbitrary guess at the new size. Assume zero or one replacements
        // ignore the pathological search == "" case.
        StringBuilder sb = new StringBuilder(original.length() + Math.max(0, replace.length() - search.length()));
        int start = 0;
        int at = original.indexOf(search);
        while (at != -1) {
            sb.append(original.substring(start, at)); // copy up to 'search'
            sb.append(replace); // insert 'replacement'
            start = at + searchLength; // skip 'search' in original
            at = original.indexOf(search, start);
        }
        if (start == 0) return original;
        sb.append(original.substring(start));
        return sb.toString();
    }

    /**
     * Replaces first count occurrences of search in original with replace.
     *
     * @param original
     * @param search
     * @param replace
     * @param count
     * @return
     */
    public static String replace(String original, String search, String replace, int count) {
        int searchLength = search.length();
        // One way of dealing with the empty string
        if (searchLength == 0) throw new IllegalArgumentException("Cannot replace empty string");
        // arbitrary guess at the new size. Assume zero or one replacements
        // ignore the pathological search == "" case.
        StringBuilder sb = new StringBuilder(original.length() + Math.max(0, replace.length() - search.length()));
        int start = 0;
        int at = original.indexOf(search);
        while (at != -1 && --count >= 0) {
            sb.append(original.substring(start, at)); // copy up to 'search'
            sb.append(replace); // insert 'replacement'
            start = at + searchLength; // skip 'search' in original
            at = original.indexOf(search, start);
        }
        if (start == 0) return original;
        sb.append(original.substring(start));
        return sb.toString();
    }

    /**
     * The substring function Sun should have defined. Negative numbers are
     * end-relative, longer-than-the-end is equivalent to the end, crossed
     * indices result in an empty string.
     */
    public static String substring(String s, int start, int end) {
        int l = s.length();
        if (start > l) start = l;
        if (end > l) end = l;
        if (start < 0) start = l + start;
        if (end < 0) end = l + end;
        if (start < 0) start = 0;
        if (end < 0) end = 0;
        if (start > end) start = end;
        return s.substring(start, end);
    }

    public static int commonPrefixLength(String s1, String s2) {
        int i = 0;
        while (i < s1.length() && i < s2.length() && s1.charAt(i) == s2.charAt(i)) {
            i++;
        }
        return i;
    }

    public static int commonPrefixLengthCI(String s1, String s2) {
        int i = 0;
        while (i < s1.length() && i < s2.length() &&
               Character.toUpperCase(s1.charAt(i)) == Character.toUpperCase(s2.charAt(i))) {
            i++;
        }
        return i;
    }

    /**
     * Returns a BufferedReader for the file f, with encoding assumed to be UTF-8.
     *
     * @throws FileNotFoundException
     */
    static public BufferedReader utf8BufferedFileReader(File f) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(f), Charset.forName("UTF-8")));
    }

    /**
     * Returns a BufferedWriter for a file with name filename, creating
     * directories as necessary to Make It So.
     * 
     * @param filename
     * @return
     * @throws IOException
     */
    public static Pair<FileWriter, BufferedWriter> filenameToBufferedWriter(String filename) throws IOException {
        FileWriter fw;
        try {
            fw = new FileWriter(filename);
        } catch (IOException ex) {
            // Probably the directory did not exist, therefore, make it so.
            // ONLY DEAL IN SLASHES.  THAT WORKS WITH WINDOWS.
            int last_slash = filename.lastIndexOf('/');
            if (last_slash == -1)
                throw ex;
            String dir = filename.substring(0, last_slash);
            ensureDirectoryExists(dir);
            fw = new FileWriter(filename);
        }
        return new Pair(fw, new BufferedWriter(fw));
    }

    static public boolean olderThanOrMissing(String resultFile, String inputFile) throws FileNotFoundException {
        File res = new File(resultFile);
        File inp = new File(inputFile);
        // res does not exist, OR res has a smaller birthday.
        if (!inp.exists()) throw new FileNotFoundException(inputFile);
        return olderThanOrMissing(res, inp.lastModified());
    }

    static public boolean olderThanOrMissing(String resultFile, long inputFileDate) {
        File res = new File(resultFile);
        return olderThanOrMissing(res, inputFileDate);
    }

    static public boolean olderThanOrMissing(File resultFile, long inputFileDate) {
        return !resultFile.exists() || (resultFile.lastModified() < inputFileDate);
    }

    // public static int compareClasses(Object x, Object y) {
    //     Class<? extends Object> a = x.getClass();
    //     Class<? extends Object> b = y.getClass();
    //     if (a == b) return 0;
    //     if (a.isAssignableFrom(b)) return -1;
    //     if (b.isAssignableFrom(a)) return 1;
    //     return a.getName().compareTo(b.getName());
    // }

    public final static Pattern envVar = Pattern.compile("[$][{][-A-Za-z0-9_.]+[}]");

    final static int INTRO_LEN = 2;

    final static int OUTRO_LEN = 1;

    /**
     * Perform variable replacement on e, where variable references match the
     * pattern "[$][{][-A-Za-z0-9_.]+[}]" and the variable name referenced is
     * contained between the curly braces.  For example, "My home is ${HOME}".
     * <p/>
     * The replacement value for the variable is obtained by consulting first
     * the system properties, then the environment.
     *
     * @param e
     * @return
     */
    public static String substituteVars(String e) {
        return substituteVars(e, envVar, INTRO_LEN, OUTRO_LEN);
    }

    /**
     * Perform variable replacement on e, where variable references match the
     * pattern "[$][{][-A-Za-z0-9_.]+[}]" and the variable name referenced is
     * contained between the curly braces.  For example, "My home is ${HOME}".
     * <p/>
     * The replacement value for the variable is obtained by consulting the
     * supplied StringMap.
     *
     * @param e
     * @return
     */
    public static String substituteVars(String e, StringMap map) {
        return substituteVars(e, envVar, INTRO_LEN, OUTRO_LEN, map);
    }

    public static String substituteVarsCompletely(String e, StringMap map, int limit) {
        return substituteVarsCompletely(e, map, limit, envVar, INTRO_LEN, OUTRO_LEN);
    }
        
    public static String substituteVarsCompletely(String e, StringMap map, int limit, Pattern vpat, int v_intro, int v_outro) {
        String initial_e = e;
        String old_e = e;
        e = substituteVars(e, vpat, v_intro, v_outro, map);
        while (old_e != e && limit-- > 0) {
            old_e = e;
            e = substituteVars(e, vpat, v_intro, v_outro, map);
        }
        if (limit <= 0) {
            throw new Error(
                    "String substitution failed to terminate, input=" + initial_e + ", " + " non-final result=" + e);
        }
        return e;
    }

    public static final StringMap sysMap = new StringMap.ComposedMaps(new StringMap.FromSysProps(), new StringMap.FromEnv());

    public static String substituteVars(String e, Pattern varPat, int intro_len, int outro_len) {
        return substituteVars(e, varPat, intro_len, outro_len, sysMap);
    }

    public static String substituteVars(String e, Pattern varPat, int intro_len, int outro_len, StringMap map) {
        Matcher m = varPat.matcher(e);

        int lastMatchEnd = 0;
        StringBuilder newE = null;
        while (m.find()) {
            if (newE == null) newE = new StringBuilder();
            MatchResult mr = m.toMatchResult();
            newE.append(e.substring(lastMatchEnd, mr.start()));
            lastMatchEnd = mr.end();
            String toReplace = e.substring(mr.start() + intro_len, mr.end() - outro_len);
            String candidate = map.get(toReplace);
            if (candidate == null) candidate = "";
            newE.append(candidate);
        }
        if (newE != null) {
            newE.append(e.substring(lastMatchEnd));
            e = newE.toString();
        }
        return e;
    }

    public static String ensureDirectoryExists(String s) throws Error {
        File f = new File(s);
        if (f.exists()) {
            if (f.isDirectory()) {
                // ok
            } else {
                throw new Error("Necessary 'directory' " + s + " is not a directory");
            }
        } else {
            if (f.mkdirs()) {
                // ok
            } else {
                throw new Error("Failed to create directory " + s);
            }
        }
        return s;
    }
    
    public static void use(Object o) {}
    public static void use(int i) {}
    public static void use(boolean b) {}
}
