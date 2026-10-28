package org.antcamarasa.map.course;

import java.util.Iterator;
import java.util.function.Function;

public abstract class MyAbstractMap<K, V> implements MyMap<K, V> {

    // 1. Un implémentation de la méthode isEmpty, fondée sur l’implémentation supposée
    // de size() dans la classe concrète, obligatoire grace a l'interface.
    @Override
    public boolean isEmpty(){
        return size() == 0;
    }


    // 2. Classe composite qui permet de regrouper K et V dans un seul conteneur.
    protected static class Entry<K, V> implements MyEntry<K, V>{
        K key;
        V value;

        public Entry(K key, V value){
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey(){
            return this.key;
        }

        @Override
        public V getValue(){
            return this.value;
        }
    }

    // Support for public keySet method...
    public Iterable<K> keySet(){
        class KeyIterator implements Iterator<K>{
            Iterator<MyEntry<K, V>> entries = entrySet().iterator();

            @Override
            public boolean hasNext(){return entries.hasNext();}

            @Override
            public K next(){return entries.next().getKey();}
        }
        class KeyIterable implements Iterable<K>{
            public Iterator<K> iterator(){
                return new KeyIterator();
            }
        }
        return new KeyIterable();
    }

    // Support for public values method...
    public Iterable<V> values(){
        class ValueIterator implements Iterator<V>{
            Iterable<MyEntry<K, V>> entries = entrySet();
            @Override
            public boolean hasNext(){
                return entries.iterator().hasNext();
            }

            @Override
            public V next(){
                return entries.iterator().next().getValue();
            }
        }

        class ValueIterable implements Iterable<V>{
            @Override
            public Iterator<V> iterator(){
                return new ValueIterator();
            }
        }
        return new ValueIterable();
    }

    // Helper generics
    public Iterable<K> keySetV2(){return new HelperIterable<K>(MyEntry::getKey);}
    public Iterable<V> valuesV2(){return new HelperIterable<>(MyEntry::getValue);}

    public class HelperIterable<T> implements Iterable<T>{
        private Function<MyEntry<K, V>, T> extractor;

        public HelperIterable(Function<MyEntry<K, V>, T> extractor){
            this.extractor = extractor;
        }

        @Override
        public Iterator<T> iterator() {
            return new HelperIterator<>(extractor);
        }
    }
    public class HelperIterator<T> implements Iterator<T>{
        Iterator<MyEntry<K, V>> entries = entrySet().iterator();
        Function<MyEntry<K, V>, T> extractor;

        public HelperIterator(Function<MyEntry<K, V>, T> extractor){
            this.extractor = extractor;
        }

        @Override
        public boolean hasNext(){
            return entries.hasNext();
        }

        @Override
        public T next(){
            return extractor.apply(entries.next());
        }
    }


    /**
     * Version évolué :
     *     private static <T, R> Iterable<R> map(
     *             Iterable<T> source,
     *             Function<? super T, ? extends R> mapper) {
     *
     *         return () -> new Iterator<R>() {
     *             private final Iterator<T> iterator = source.iterator();
     *
     *             @Override
     *             public boolean hasNext() {
     *                 return iterator.hasNext();
     *             }
     *
     *             @Override
     *             public R next() {
     *                 return mapper.apply(iterator.next());
     *             }
     *         };
     *     }
     *
     *     @Override
     *     public Iterable<K> keySet() {
     *         return map(entrySet(), MyEntry::getKey);
     *     }
     *
     *     @Override
     *     public Iterable<V> values() {
     *         return map(entrySet(), MyEntry::getValue);
     *     }
     * }
     */
}
