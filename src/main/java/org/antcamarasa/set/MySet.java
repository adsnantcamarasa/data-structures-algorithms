package org.antcamarasa.set;

public interface MySet<E> extends Iterable<E> {
    /**
     * Un Set interdit les doublons. Chaque élément <T> qu'il contient doit obligatoirement être unique.
     * Si l'on essaye d'ajouter un élément qui existe déjà, le Set refusera tout simplement l'ajout.
     *
     * Un set n'est pas une liste! :
     *  -> Une liste maintient un ordre précis(le premier élément inséré reste à l'indice 0, le deuxième a l'indice 1...)
     *  -> Un set, dans sa définition générale, ne garanti aucun ordre particulier et n'utilise pas d'index.
     *
     *  Comme un Set est une interface, on ne peux pas l'sintancier directement. On utilise des classes concrètes selon nos besoins :
     *  1. HashSet : C'est la plus utilisée. Elle stocke les éléments dans un désordre total,
     *               mais offre des performances ultra-rapides pour ajouter, supprimer
     *               ou chercher un élément.
     *
     *  2. LinkedHashSet : C'est le compromis. Elle garanti l'unicité, mais retient l'ordre d'inserions des éléments.
     *
     *  3. TreeSet : Elle garanti l'unicité et trie automatiquement les éléments(par exemple par ordre alphabétique ou numérique)
     *
     *  L'interface SET :
     *
     *  1. Les operations individuelles (Ajout, Suppresion, Verification)
     *  > boolean add(E e)
     *    Ajoute l'élément s'il n'est pas déjà présent. Elle renvoie un booléen : true si l'élément a été ajouté,
     *                                                et false si l'élément y était déjà (l'ajout échoue pour préserver l'unicité).
     *
     *  > boolean remove(E e)
     *    Supprime l'élément spécifié s'il est présent (renvoie true si le Set a été modifié).
     *
     *  > boolean contains(E e)
     *    Vérifie si le Set contient l'élément demandé(renvoie True, ou false)
     *
     *  2. Les operations de groupe
     *  3. L'état du SET
     *     > int size()
     *     Renvoie le nombre d'éléments uniques contenus dans le set
     *
     *     > boolean isEmpty()
     *     Renvoie true si le Set ne contient aucun élément.
     *
     *     > void clear()
     *     vide complètement le set.
     *
     *   4. Extrcation et parcours
     *      > E Iterator()
     *      Renvoie un objet Iterator permettant de parcourir un a un les éléments du SET. très utilie car il n'y a pas d'indexation numérique possible.
     *
     *      > E[] toArray
     *      Transforme le set en un tableau
     */

    boolean add(E e);
    boolean remove(E e);
    boolean contains(E e);

    int size();
    boolean isEmpty();
}
