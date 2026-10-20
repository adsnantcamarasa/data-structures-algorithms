package org.antcamarasa.fundamental.iterator;

import java.util.Collection;
import java.util.Iterator;

public class SnapshotIterator<E> implements Iterable<E> {
    // Ici je crée un snapshot de Iterator.
    Collection<E> collection;

    public SnapshotIterator(Collection<E> collection){
        this.collection = collection;
    }

    @Override
    public Iterator<E> iterator(){
        return new InnerIterator<E>();
    }


    // Le probleme que j'ai ai que je ne sais pas quelle collection j'ai.. donc si j'ai une liste ou Map ou autre chose, il faut bien que j'aille chercher
    // quelque part comment parcourir la structure.
    // Tyipiquement si Collection est une liste ? alors je garde en mémoire un index. Mais si c'est une Map... Alors non.

    private class InnerIterator<E> implements Iterator<E> {
        E next;

        public InnerIterator(){
            this.next = null;
        }



        @Override
        public boolean hasNext(){
            return false;
        }

        @Override
        public E next(){
            return null;
        }

    }
}
