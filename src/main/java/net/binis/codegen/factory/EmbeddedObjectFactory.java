package net.binis.codegen.factory;

@FunctionalInterface
public interface EmbeddedObjectFactory<T> {

    Object create(Object parent, T value);

}
