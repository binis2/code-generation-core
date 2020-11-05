package net.binis.codegen.collection;

public interface CodeList<T, R> {

    CodeList<T, R> add(T value);
    R and();

}
