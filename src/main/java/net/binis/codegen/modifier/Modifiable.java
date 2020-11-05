package net.binis.codegen.modifier;

@FunctionalInterface
public interface Modifiable<T> {
    T with();
}
