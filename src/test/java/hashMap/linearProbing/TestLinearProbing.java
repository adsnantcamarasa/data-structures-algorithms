package hashMap.linearProbing;

import org.antcamarasa.map.naive.hashMap.Entry;
import org.antcamarasa.map.naive.hashMap.linearProbing.LinearProbing;
import org.antcamarasa.set.MySet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;


public class TestLinearProbing {
    private LinearProbing<String, Integer> myHashMap;

    @BeforeEach
    public void setUp() {
        this.myHashMap = new LinearProbing<>();
    }



    //________________________________________ CAS DE TEST ________________________________________
    // I.   Insertion simple & CRUD
    // II.  Collisions &  gestion du tableau (bucket, dist, size)
    // III. Gestion des TOMBSTONES
    // IV.  Redimension & ReHash


    // 1. Simple insertion
    @Test
    public void testSimpleInsertion() {
        myHashMap.put("Antoine", 10);
        myHashMap.put("Enzo", 15);
        myHashMap.put("Laurent", 20);

        Assertions.assertEquals(3, myHashMap.getSize());
    }

    // 2. Insert with same index and not generate collision
    //    Check correctness of size
    //    Check correctness of dist
    @Test
    public void testInsertWithSameIndex(){
        myHashMap.put("Antoine", 10);
        myHashMap.put("Leo", 15);
        myHashMap.put("Antoinea", 22);

        int[] dist = myHashMap.getDist();
        Assertions.assertEquals(1, dist[myHashMap.getIndexFromKey("Antoine")]);
    }

    // 3. Check same key.
    //    Replace old value.
    //    Not increment size.
    @Test
    public void testSameKey(){
        myHashMap.put("Antoine", 10);
        myHashMap.put("Antoine", 15);

        Assertions.assertEquals(1, myHashMap.getSize());
        Assertions.assertEquals(15, myHashMap.get("Antoine"));
    }

    // 4. ReHash load Factor | Last test
    @Test
    public void testReHash(){
        myHashMap.put("Antoine", 10);
        myHashMap.put("Leo", 15);
        myHashMap.put("Antoinea", 22);
        myHashMap.put("Enzo", 35);

        Assertions.assertEquals(10, myHashMap.getCapacity());
        Assertions.assertEquals(4, myHashMap.getSize());
    }

    // 5. Test keys Iterator
    @Test
    public void testKeysIterators(){
        myHashMap.put("Antoine", 10);
        myHashMap.put("Hervet", 40);
        myHashMap.put("Jacob", 13);

        LinearProbing.MyKeysView myKeysView = myHashMap.getKeySet();
        Iterator<String> it = myKeysView.iterator();

        while(it.hasNext()){
            String next = it.next();
            System.out.println(next);
        }
    }

    // 6. Test values Iterator
    @Test
    public void testValuesIterator(){
        myHashMap.put("Antoine", 10);
        myHashMap.put("Leo", 15);
        myHashMap.put("Antoinea", 22);
        myHashMap.put("Enzo", 35);

        LinearProbing.ValuesView valuesView = myHashMap.new ValuesView();
        Iterator<Integer> it = valuesView.iterator();
        while (it.hasNext()){
            int value = it.next();
            System.out.println("Value : " + value);
        }
    }

    // 7. Test Entry Iterator
    @Test
    public void testEntryIterator(){
        myHashMap.put("Antoine", 12);
        myHashMap.put("Fred", 44);
        myHashMap.put("Alfonse", 17);
        myHashMap.put("Mira", 7);

        MySet<Entry<String, Integer>> entries = myHashMap.new MyEntryView();
        Iterator<Entry<String, Integer>> it = entries.iterator();

        while(it.hasNext()){
            System.out.println("**********************");
            Entry<String, Integer> entry = it.next();
            System.out.println("Key :" + entry.key());
            System.out.println("Value : " + entry.value());
            System.out.println();
        }
    }


    @Test
    public void testKeySet(){
        myHashMap.put("Antoine", 10);

    }
}
