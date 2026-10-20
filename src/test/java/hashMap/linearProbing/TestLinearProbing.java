package hashMap.linearProbing;

import org.antcamarasa.map.hashMap.linearProbing.LinearProbing;
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

    // 5. Test Iterator
    @Test
    public void testIterators(){
        myHashMap.put("Antoine", 10);
        myHashMap.put("Hervet", 40);
        myHashMap.put("Jacob", 13);

        LinearProbing.MyKeySet myKeySet = myHashMap.getKeySet();
        Iterator<String> it = myKeySet.iterator();

        while(it.hasNext()){
            String next = it.next();
            System.out.println(next);
        }
    }
}
