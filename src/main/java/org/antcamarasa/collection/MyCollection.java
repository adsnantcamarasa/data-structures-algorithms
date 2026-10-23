package org.antcamarasa.collection;

public interface MyCollection<T> extends Iterable<T>{
    int size();
    boolean isEmpty();
    boolean contains(T t);

    void add(T t);
    boolean remove(T t);
}
