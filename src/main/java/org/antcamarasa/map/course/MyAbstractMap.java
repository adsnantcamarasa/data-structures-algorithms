package org.antcamarasa.map.course;

public abstract class MyAbstractMap implements MyMap {

    public boolean isEmpty(){
        // Ici, comment se baser sur le sise d'une supposé implémentation conréte.
        // en gros super va vers le parent mais comment on va vers l'enfant ?
        return false;
    }

}
