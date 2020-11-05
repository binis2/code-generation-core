package net.binis.codegen.collection;

public interface CodeSet<T, R> {

    CodeSet<T, R> add(T value);
    R and();

}
