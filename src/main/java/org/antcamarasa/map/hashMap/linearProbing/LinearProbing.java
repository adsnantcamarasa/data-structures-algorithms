package org.antcamarasa.map.hashMap.linearProbing;

import org.antcamarasa.set.MySet;

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

    public class MyKeySet implements MySet<K>{
        @Override
        public boolean add(K k) {
            return false;
        }

        @Override
        public boolean remove(K k) {
            return false;
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


    // __________________________________ Getter ___________________________________
    public int[] getDist(){return this.dist;}
    public int getSize(){return this.size;}
    public int getCapacity(){return this.capacity;}
    public MyKeySet getKeySet(){
        return new MyKeySet();
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
