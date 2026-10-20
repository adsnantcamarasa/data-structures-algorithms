package org.antcamarasa.fundamental.iterator;

import java.lang.reflect.Array;
import java.util.*;

public class SnapshotIterator<E> implements Iterable<E> {
    // Ici je crée un snapshot de Iterator.
    private E[] collectionSnapShot;

    public SnapshotIterator(Collection<E> collection){
        this.collectionSnapShot = (E[]) collection.toArray();
    }

    @Override
    public Iterator<E> iterator(){
        return new InnerIterator();
    }

    private class InnerIterator implements Iterator<E> {
        private Integer index = 0;
        private boolean removal = false; // Not available at begin.

        @Override
        public boolean hasNext(){
            return this.index < SnapshotIterator.this.collectionSnapShot.length;
        }

        @Override
        public E next(){
            if(index == SnapshotIterator.this.collectionSnapShot.length) throw new NoSuchElementException("No next element");
            removal = true;
            return SnapshotIterator.this.collectionSnapShot[this.index++];
        }

        public void remove(){
            if(!this.removal) throw new IllegalStateException("Remove is not availbale");

            // Shifting all element to one size from left.
            int currentIndex = index - 1; // on revient un élément en retrait après avoir tout supprimé
            int i = index - 1;
            int j = index;
            while (j < SnapshotIterator.this.collectionSnapShot.length){
                SnapshotIterator.this.collectionSnapShot[i++] = SnapshotIterator.this.collectionSnapShot[j++];
            }

            E[] temp = (E[]) new Object[SnapshotIterator.this.collectionSnapShot.length -1];
            for(int idx = 0; idx < temp.length; idx++){
                temp[idx] = SnapshotIterator.this.collectionSnapShot[idx];
            }
            SnapshotIterator.this.collectionSnapShot = temp;
            this.removal = false;
            this.index = currentIndex;
        }
    }
}
