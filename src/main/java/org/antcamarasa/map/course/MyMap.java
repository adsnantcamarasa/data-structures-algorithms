package org.antcamarasa.map.course;

public interface MyMap<K, V> {
    // Renvoie le nombre d'entrées contenues dans M.
    int size();

    // Renvoie un booléan indiquant si M est vide
    boolean isEmpty();

    // Renvoie la valeur v associé à la clé K si une telle entrée existe; sinon null.
    V get(K k);

    // Si M ne possède pas d'entrée donct la clé est égale à K, alors ajoute l'entré et renvoie null;
    // sinon, remplace par v la valeur existante sur l'entrée dont la clé est égale à k et retourne l'ancienne v.
    V put(K k, V v);

    // Supprime de M l'entrée dont la clé est égale à K, et renvoie sa valeur, si M ne contient pas une telle entrée, renvoie null.
    V remove(K k);

    // Renvoie une collection itérable contenant toutes les clés stockées dans M
    Iterable<K> keySet();

    // Renvoie une collection itérable contenant toues les valeurs des entrées stockées dans M (avec doublons si plusieurs clés sont associées à la même valeur.)
    Iterable<V> values();

    // Renvoie une collection itérable contenant toutes les entrées(paires clé-valeur) de M.
    Iterable<MyEntry<K, V>> entrySet();
}
