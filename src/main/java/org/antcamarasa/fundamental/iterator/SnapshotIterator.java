package org.antcamarasa.fundamental.iterator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

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

    // Le probleme que j'ai ai que je ne sais pas quelle collection j'ai.. donc si j'ai une liste ou Map ou autre chose, il faut bien que j'aille chercher
    // quelque part comment parcourir la structure.
    // Tyipiquement si Collection est une liste ? alors je garde en mémoire un index. Mais si c'est une Map... Alors non.

    // Deux options :
    // 1. Standardiser le snapshot au moment de la création : peu importe le type de collection passé au constructeur, on le converti en une structure lineaire standard et facielement parcourable(ArrayList, Arrau)

    private class InnerIterator implements Iterator<E> {
        private E next;
        private Integer index;

        @Override
        public boolean hasNext(){
            // Si next n'a jamais été appelé.
            // Vérification : next est null & index n'est pas -1 signifie fin de la collecion & la collection standardisé en array n'est pas vide.
            if(this.index == null && SnapshotIterator.this.collectionSnapShot.length > 0){
                return true;
            }

            // Si next a déja été utilisé, j'ai deux option soit next tant qu'il n'est pas null ou index tant qu'il n'est pas = -1
            return this.next != null;
            // return this.index != -1;
        }

        @Override
        public E next(){
            // si hasNext et qu'on est au premier tour.
            if(this.index == null){
                this.index = 0;
                this.next  = SnapshotIterator.this.collectionSnapShot[index];
            }
            else if(index + 1 >= SnapshotIterator.this.collectionSnapShot.length){
                this.next = null;
                this.index = -1;
            }
            else {
                E current = SnapshotIterator.this.collectionSnapShot[index];
                this.index = this.index + 1;
                this.next  = SnapshotIterator.this.collectionSnapShot[index];
                return current;
            }

            return this.next;
        }

    }


}
