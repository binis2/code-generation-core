package net.binis.codegen.tools;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class Reflection {

    private Reflection() {

    }

    @SneakyThrows
    public static <T> T instantiate(Class<T> cls) {
        return cls.getDeclaredConstructor().newInstance();
    }

    public static Field findField(Class<?> cls, String name) {
        try {
            return cls.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object obj, String name) {
        try {
            var field = findField(obj.getClass(), name);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception e) {
            log.error("Unable to get value for field {} of {}", name, obj.getClass().getName(), e);
            return null;
        }
    }

}
