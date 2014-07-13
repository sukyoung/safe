/*******************************************************************************
    Copyright 2010, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This data structure is designed to optimize a costly bit of Scala code.
 * An ArrayBackedList is like a fixed-size ArrayList, except that it
 * uses the array passed in to the constructor.  It is the caller's
 * responsibility to ensure that this array is appropriately (un)shared.
 *
 * The client is kr.ac.kaist.jsaf.scala_src.useful.Lists.toList.
 *
 * @author dr2chase
 */
public class ArrayBackedList<E> extends AbstractList<E> implements List<E> {

    public static  <T> List<T>fromImmutable(Collection<T> collection) {
        if (collection instanceof List)
            return (List<T>) collection;
        else
            return new ArrayBackedList<T>(collection);
    }

    E[] elements;
    int offset;
    int size;

    public ArrayBackedList(Collection<E> collection) {
        this((E[])collection.toArray(), 0, collection.size());
    }

    public ArrayBackedList(E[] elements) {
        this(elements, 0, elements.length);
    }

    public ArrayBackedList(E[] elements, int offset, int size) {
        this.elements = elements; this.offset = offset; this.size = size; 
    }

    @Override
    public E get(int index) {
        if (index >= size)
            throw new ArrayIndexOutOfBoundsException(index);
        return elements[offset+index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        // TODO Auto-generated method stub
        return new Iterator<E>() {

            int nextIndex = 0;

            @Override
            public boolean hasNext() {
                return nextIndex < size;
            }

            @Override
            public E next() {
                if (hasNext())
                    return get(nextIndex++);
                throw new java.util.NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("ArrayBackedLists do not support remove");
            }

        };
    }

    @Override
    public E set(int index, E element) {
        if (index >= size)
            throw new ArrayIndexOutOfBoundsException(index);
        E previous = elements[offset+index];
        elements[offset+index] = element;
        return previous;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex == toIndex)
            return Collections.emptyList();
        else if (fromIndex < 0)
            throw new IndexOutOfBoundsException("negative fromIndex " + fromIndex);
        else if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex ("+ toIndex +") > size ("+ size +")");
        else if (fromIndex > toIndex)
            throw new IndexOutOfBoundsException("fromIndex ("+ fromIndex +") > toIndex ("+ toIndex +")");
        return new ArrayBackedList<E>(elements, offset + fromIndex, toIndex - fromIndex);
    }
}
