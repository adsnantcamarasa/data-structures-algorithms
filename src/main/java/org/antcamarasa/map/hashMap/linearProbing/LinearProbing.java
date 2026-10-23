package org.antcamarasa.map.hashMap.linearProbing;

import org.antcamarasa.collection.MyCollection;
import org.antcamarasa.map.Entry;
import org.antcamarasa.set.MySet;

import javax.sound.sampled.Line;
import java.util.*;
import java.util.function.Function;

public class LinearProbing<K, V> {
    private MyNode<K, V>[] bucket;
    private int[] dist;

    private int capacity = 5;
    private int size = 0;
    private final double MAX_RATIO = 0.7;


    // DEFUNCT CORRESPOND A UNE INSTANCE UNIQUE
    private final MyNode<K, V> TOMBSTONE = new MyNode<>(null, null);
    static class MyNode<K, V> {
        K key;
        V value;

        public MyNode(K key, V value){
            this.key   = key;
            this.value = value;
        }

        public K getKey(){return this.key;}
        public V getValue(){return this.value;}
    }




    @SuppressWarnings("unchecked")
    public LinearProbing(){
        this.bucket = (MyNode<K, V>[]) new MyNode[capacity];
        this.dist   = new int[capacity];
    }

    public V put(K key, V value){
        int index = myCompressionFunction(myHashFunction(key));

        // Cas 1 | bucket[index] = null
        if(this.bucket[index] == null){
            bucket[index] = new MyNode<>(key, value);
            this.size = this.size + 1;
            tryRehash();
            return value;
        }

        return helperRecursive(index, index, null, 0, key, value);
    }
    private V helperRecursive(int startIndex, Integer currentIndex, Integer tombstoneIndex, int counter, K key, V value){
        // Prevent circular array | if we go index out of bounds we set index to .
        currentIndex = currentIndex >= this.capacity ? 0 : currentIndex;

        // Prevent for infinite loop.
        if(counter >= this.capacity){
            throw new IllegalStateException("Impossible to add node");
        }

        // Base case
        if(bucket[currentIndex] == null){
            // 1. On crée la node à insérer.
            MyNode<K, V> node = new MyNode<>(key, value);


            if(tombstoneIndex == null){
                bucket[currentIndex] = node;
            } else {
                bucket[tombstoneIndex] = node;
            }

            // On update dist[startIndex] en prenant la distance max entre startIndex et counter.
            dist[startIndex] = Math.max(dist[startIndex], counter);
            this.size = this.size + 1;
            tryRehash();
            return value;
        }

        if(bucket[currentIndex] != this.TOMBSTONE && bucket[currentIndex].key.equals(key)){
            V oldValue = bucket[currentIndex].value;
            bucket[currentIndex].value = value;
            return oldValue;
        }

        tombstoneIndex = bucket[currentIndex] == this.TOMBSTONE && tombstoneIndex == null ? currentIndex : tombstoneIndex;
        return helperRecursive(startIndex, currentIndex + 1, tombstoneIndex, counter + 1, key, value);
    }

    public V get(K key) {
        return findHelper(key, (idx) -> bucket[idx].value);
    }
    public V remove(K key){
        return findHelper(key, (idx) -> {
            V oldValue  = bucket[idx].value;
            bucket[idx] = this.TOMBSTONE;
            this.size--;
            return oldValue;
        });
    }

    public boolean containsKey(K key){
        V searchValue = findHelper(key, (idx) -> bucket[idx].value);
        return searchValue != null;
    }
    public boolean containsValue(V value){return true;}


    private V findHelper(K key, Function<Integer, V> myFunction){
        int index = myCompressionFunction(myHashFunction(key));
        int counter = 0;
        int borne = dist[index];


        while (counter <= borne){
            index = index >= this.capacity ? 0 : index;

            if(bucket[index] == null) break;
            if(bucket[index] == this.TOMBSTONE){index++; counter++; continue;};
            if (bucket[index].key.equals(key)){return myFunction.apply(index);}

            counter++;
            index++;
        }
        throw new NoSuchElementException("Element not existing in map");
    }


    // _________________________________ Inner class _______________________________
    // 1. keySet()   - List all key - Elle doit renvoyer une vue.
    // 2. values()   - List all values
    // 3. entrySet() - List all key value Pair

    // 1. keySet()   - List all key - Elle doit renvoyer une vue.
    public class MyKeysView implements MySet<K>{
        @Override
        public boolean add(K k) {
            return false;
        }

        @Override
        public boolean remove(K k) {
            return LinearProbing.this.remove(k) != null;
        }

        @Override
        public boolean contains(K k) {
            return containsKey(k);
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public Iterator<K> iterator(){
            return new MyCustomKeyIterator();
        }
    }
    public MySet<K> keySet(){
        return new MyKeysView();
    }

    // 2. values()   - List all values
    public class ValuesView implements MyCollection<V>{
        @Override
        public int size(){
            return LinearProbing.this.size;
        }

        @Override
        public boolean isEmpty(){
            return LinearProbing.this.size == 0;
        }

        @Override
        public void add(V v){
            throw new IllegalArgumentException("Cannot add element in view");
        }

        @Override
        public boolean contains(V v){
            // Not implemented
            return false;
        }

        @Override
        public boolean remove(V v){
            // Not implemented
            return false;
        }

        @Override
        public Iterator<V> iterator(){
            return new MyValuesIterator();
        }
    }
    public MyCollection<V> values(){
        return new ValuesView();
    }

    // 3. entrySet() - List all key value Pair
    public class MyEntryView implements MySet<Entry<K, V>> {
        @Override
        public boolean add(Entry<K, V> entry){
            throw new IllegalArgumentException("Cannot add element to a view");
        }

        @Override
        public boolean remove(Entry<K, V> entry){
            throw new IllegalArgumentException("Cannot remove from a view");
        }

        @Override
        public boolean contains(Entry<K, V> entry){
            return LinearProbing.this.containsKey(entry.key()) && LinearProbing.this.containsValue(entry.value());
        }

        @Override
        public int size(){
            return LinearProbing.this.size;
        }

        @Override
        public boolean isEmpty(){
            return LinearProbing.this.size == 0;
        }

        public Iterator<Entry<K, V>> iterator(){
            return new MyEntryIterator();
        }
    }
    public MySet<Entry<K, V>> entry(){return new MyEntryView();}


    // 4. On methode for keySet and entrySet

    /**
     *     boolean add(E e);
     *     boolean remove(E e);
     *     boolean contains(E e);
     *
     *     int size();
     *     boolean isEmpty();
     */
    public class SetView implements MySet<Entry<K, V>>{
        // Je vais établir une fonction qui sera utilisé pour ce que je sougaite donc, soit une K soit une Entry K, V
        // Deja j'aimerais avoir une comprehension clair, des argument passé en add.
        // en gros je lui passe soit une key est une valeur et je crée un entry, soit je lui passe une entry directement et je l'ajoute ?

        public boolean add(K k, V v){
            throw new RuntimeException("Cannot add in view");
        }

        public boolean addOtherWay(K k, V v){
            throw new RuntimeException("Cannot add in view");
        }

        public boolean remove(Entry<K, V> entry){
            throw new RuntimeException("Cannot remove in a view");
        }

        public boolean contain(Entry<K, V> entry){
            return LinearProbing.this.containsKey(entry.key()) && LinearProbing.this.containsValue(entry.value());
        }

        public boolean containOtherWay(K k, V v){
            return LinearProbing.this.containsKey(k) && LinearProbing.this.containsValue(v);
        }

        public int size(){
            return LinearProbing.this.size;
        }

        public boolean isEmpty(){
            return size() == 0;
        }

        // Il faudrait un moyen pour créer un sur Type qui peux soit être K spot Entry<K, V> est-ce possible en java ?
        public Iterator<Entry<K, V>> iterator(){
            return new GenericIterator("fonction a passer");
        }

    }

    // Pour rendre générique cette classe je dois pouvoir retourner soit une Entry<K, V> soit une K depuis depuis la méthode next
    // La solution en tête passer en argument du constructeur une lambda qui me dis tu retourne K ou tu retourne Entry<K,V>
    // toute la logique elle n'est pas a passer
    public class GenericIterator() extends Iterator<Entry<K, V>> {
        private int index;
        private int counter;

        public GenericIterator(String functionAPasser) {
            this.index = 0;
            this.counter = 0;
        }


        public boolean hasNext(){
            return this.index < LinearProbing.this.size;
        }

        public Entry<K, V> next(){
            while (this.index < LinearProbing.this.size){
                if(LinearProbing.this.bucket[index] != null && LinearProbing.this.bucket[index] != LinearProbing.this.TOMBSTONE){
                    this.counter++;
                    // return la fonction à l'index et incrémente index.
                }
            }

            throw new RuntimeException("End of iterator!");
        }
    }





    // _____________ ITERATOR __________
    private class MyKeyIterator implements Iterator<K>{
        // Ici il me faut un moyen de savoir ou est-ce que j'en suis dans la stucture de bucket.
        // Il me faudrait => counter pour savoir si j'ai données toutes les valeurs possibles.

        int counterOfFindElement = 0;
        int currentIndex = 0;

        @Override
        public boolean hasNext(){
            return counterOfFindElement + 1 < size;
        }

         @Override
         public K next(){
            currentIndex = findNextElementHelper(currentIndex);
            counterOfFindElement++;
            return bucket[currentIndex].key;

         }


         private int findNextElementHelper(int currentIndex){
            int innerCounter = 0;
            while (innerCounter < size){
                currentIndex = currentIndex >= size ? 0 : currentIndex;

                if(bucket[currentIndex] != null && bucket[currentIndex] != TOMBSTONE){
                    return currentIndex;
                }

                currentIndex++;
                innerCounter++;
            }

            throw new RuntimeException("Impossible to find next element");
         }
    }
    private class MyCustomKeyIterator implements Iterator<K>{
        private Integer counter = null;
        private Integer nextIndex = null;
        private K next;

        public MyCustomKeyIterator(){
            this.counter = 0;
            setNextIndex();

            if(this.nextIndex == null) throw new NoSuchElementException();
            this.next = LinearProbing.this.bucket[nextIndex].key;
        }


        @Override
        public boolean hasNext(){
            return this.next != null;
        }

        @Override
        public K next(){
            K current = LinearProbing.this.bucket[this.nextIndex].key;
            incrementIndex();
            setNextIndex();

            if(this.nextIndex != -1){
                this.next = LinearProbing.this.bucket[this.nextIndex] == null ? null : LinearProbing.this.bucket[this.nextIndex].key;
            }

            return current;
        }


        private void setNextIndex(){
            int previousCounter = this.counter;

            while (counter < LinearProbing.this.size - 1){
                if(nextIndex == null){this.nextIndex = 0; return;}
                nextIndex =  nextIndex >= LinearProbing.this.capacity ? 0 : nextIndex;

                if(LinearProbing.this.bucket[nextIndex] != null && LinearProbing.this.bucket[nextIndex] != LinearProbing.this.TOMBSTONE){
                    incrementCounter();
                    return;
                }

               incrementIndex();
            }

            this.nextIndex = -1;
            this.next = null;
        }

        private void incrementCounter(){
            this.counter++;
        }
        private void incrementIndex(){
            this.nextIndex++;
        }
    }
    private class MyValuesIterator implements Iterator<V>{
         int counter;
         int index;

         public MyValuesIterator(){
             this.index = 0;
             this.counter = 0;
         }

         @Override
         public boolean hasNext(){
             return this.index < LinearProbing.this.capacity;
         }

         @Override
        public V next(){
             while (index < LinearProbing.this.capacity){
                 if(LinearProbing.this.bucket[index] != null && LinearProbing.this.bucket[index] != LinearProbing.this.TOMBSTONE){
                     this.counter++;
                     return LinearProbing.this.bucket[this.index++].value;
                 }
                 this.index++;
             }
             throw new IllegalArgumentException("End of iterator");
         }

    }

    private class MyEntryIterator implements Iterator<Entry<K, V>>{
        int index = 0;
        int counter = 0;

        public class MyEntryInstance implements Entry<K, V>{
            K key;
            V value;

            public MyEntryInstance(K k, V v){
                this.key = k;
                this.value = v;
            }

            @Override
            public K key(){
                return this.key;
            }

            @Override
            public V value(){
                return this.value;
            }
        }

        @Override
        public boolean hasNext(){return this.counter < LinearProbing.this.size;}

        @Override
        public Entry<K, V> next(){
            while (this.index < LinearProbing.this.bucket.length){
                if(LinearProbing.this.bucket[index] != null && LinearProbing.this.bucket[index] != LinearProbing.this.TOMBSTONE){
                    this.counter++;
                    return new MyEntryInstance(LinearProbing.this.bucket[index].key, LinearProbing.this.bucket[index++].value);
                }

                this.index++;
            }
            return null;
        }
    }

    // __________________________________ Getter ___________________________________
    public int[] getDist(){return this.dist;}
    public int getSize(){return this.size;}
    public int getCapacity(){return this.capacity;}
    public MyKeysView getKeySet(){
        return new MyKeysView();
    }

    // __________________________________ Helper ____________________________________
    public int getIndexFromKey(K key){return myCompressionFunction(myHashFunction(key));}
    public void tryRehash(){
        if(calculateRatio() < this.MAX_RATIO) return;

        // 1. Double this.capacity
        this.capacity = this.capacity * 2;

        // 2. Créer un seule tableau temporaire temporaryBucket | Pas besoin d'utiliser dist on va looper sur tout les éléments de bucket et put chaque node
        MyNode<K, V>[] tmpBucket = this.bucket;
        this.bucket = (MyNode<K, V>[]) new MyNode[this.capacity];

        this.dist = new int[this.capacity];
        this.size = 0;

        Arrays.stream(tmpBucket)
                  .filter(Objects::nonNull)
                  .filter(el -> el != this.TOMBSTONE)
                  .forEach(el -> put(el.key, el.value));

    }
    public double calculateRatio(){return (double) this.size / this.capacity;}

    // ____________________________ Non to code function __________________________
    // from key calculate hash value;
    public long myHashFunction(K key){
        int h = key.hashCode();
        return (long) (h ^ (h >>> 16));
    }
    // from hashValue reduce through index E 0, bucket.length - 1;
    public int myCompressionFunction(long hashValue){
        return (int) Math.floorMod(hashValue, (long) this.capacity);
    }
}
