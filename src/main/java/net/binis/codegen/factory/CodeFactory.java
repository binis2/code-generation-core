package net.binis.codegen.factory;

import lombok.Builder;
import lombok.Data;
import net.binis.codegen.exception.GenericCodeGenException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CodeFactory {

    private static final Map<Class<?>, RegistryEntry> registry = new HashMap<>();

    private CodeFactory() {
        //Do nothing
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> cls) {
        var entry = registry.get(cls);
        if (entry != null) {
            return (T) entry.getImplClass().create();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <M, T, P> M modify(P parent, T value) {
        var entry = registry.get(value.getClass());
        if (entry != null) {
            return (M) entry.getModifierClass().create(parent, value);
        }
        return null;

    }

    private static Supplier<Object> tryGenerateCreator(Class<?> implClass) {
        return () -> {
            try {
                return implClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new GenericCodeGenException(e);
            }
        };
    }

    private static BiFunction<Object, Object, Object> tryGenerateModifier(Class<?> modifierClass, Class<?> entityClass) {
        try {
            var constructor = modifierClass.getDeclaredConstructor(Object.class, entityClass);
            constructor.setAccessible(true);

            return (parent, entity) -> {
                try {
                    return constructor.newInstance(parent, entity);
                } catch (Exception e) {
                    throw new GenericCodeGenException(e);
                }
            };

        } catch (Exception e) {
            throw new GenericCodeGenException(e);
        }

    }


    public static void registerEmbeddableType(Class<?> intf, ObjectFactory impl, EmbeddedObjectFactory modifier) {
        registry.put(intf, RegistryEntry.builder().implClass(impl).modifierClass(modifier).build());
    }

    @Data
    @Builder
    private static class RegistryEntry {
        private ObjectFactory implClass;
        private EmbeddedObjectFactory modifierClass;
    }
}
