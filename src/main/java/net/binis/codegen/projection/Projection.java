package net.binis.codegen.projection;

import net.binis.codegen.factory.CodeFactory;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public abstract class Projection {

    public static <T> T single(Object obj, Class<T> projection) {
        return CodeFactory.projection(obj, projection);
    }

    public static <T> List<T> list(List<?> list, Class<T> projection) {
        return (List) CodeFactory.projection(list, projection);
    }

    public static <T> Set<T> set(Set<?> set, Class<T> projection) {
        return (Set) CodeFactory.projection(set, projection);
    }

}
