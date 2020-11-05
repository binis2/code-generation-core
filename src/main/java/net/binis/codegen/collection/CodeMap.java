package net.binis.codegen.collection;

public interface CodeMap<K, V, R> {

    CodeMap<K, V, R> put(K key, V value);
    R and();

}
