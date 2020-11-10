package net.binis.codegen.modifier;

@FunctionalInterface
public interface Modifier<T> {
    void setObject(T object);
}
