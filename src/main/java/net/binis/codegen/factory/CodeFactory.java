package net.binis.codegen.factory;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 Binis Belev
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.annotation.Default;
import net.binis.codegen.exception.GenericCodeGenException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.binis.codegen.tools.Reflection.initialize;

@Slf4j
public class CodeFactory {

    private static final Map<Class<?>, RegistryEntry> registry = new HashMap<>();
    private static final Map<Class<?>, IdRegistryEntry> idRegistry = new HashMap<>();
    private static EnvelopingObjectFactory envelopingFactory;
    private static ProjectionProvider projections = initProjectionProvider();

    private static final Map<Class<?>, Map<Class<?>, ProjectionInstantiation>> projectionsCache = new HashMap<>();

    private CodeFactory() {
        //Do nothing
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> cls) {
        var entry = registry.get(cls);
        if (entry != null) {
            return internalEnvelop((T) entry.getImplFactory().create());
        } else {
            var result = defaultCreate(cls, cls);
            if (isNull(result)) {
                var parent = cls.getDeclaringClass();
                if (nonNull(parent)) {
                    result = (T) defaultCreate(cls, parent);
                }
            }
            if (isNull(result)) {
                registerType(cls, null, null);
            }
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> cls, String defaultClass) {
        var entry = registry.get(cls);
        Object obj = null;
        if (entry != null) {
            if (entry.getImplFactory() != null) {
                obj = entry.getImplFactory().create();
            } else {
                return null;
            }
        } else {
            try {
                initialize(defaultClass);
                obj = internalCreate(cls);
            } catch (Exception e) {
                log.error("Can't find class: {}", defaultClass);
            }
        }

        return internalEnvelop((T) obj);
    }


    @SuppressWarnings("unchecked")
    public static <M, T, P> M modify(P parent, T value, Class cls) {
        var entry = registry.get(cls);
        if (entry != null) {
            var obj = entry.getModifierFactory().create(parent, value);
            if (isNull(envelopingFactory)) {
                return (M) obj;
            } else {
                return (M) envelopingFactory.envelop(obj);
            }
        }
        return null;
    }

    public static Class<?> lookup(Class<?> intf) {
        var entry = registry.get(intf);

        if (isNull(entry)) {
            create(intf);
            entry = registry.get(intf);
        }

        if (entry != null) {
            if (entry.getImplClass() == null && entry.getImplFactory() != null) {
                entry.setImplClass(entry.getImplFactory().create().getClass());
            }
            return entry.getImplClass();
        }
        return null;
    }

    public static IdDescription lookupId(Class<?> intf) {
        return idRegistry.get(intf);
    }

    public static void registerId(Class<?> cls, String fieldName, Class<?> fieldType) {
        idRegistry.computeIfAbsent(cls, k -> IdRegistryEntry.builder().name(fieldName).type(fieldType).build());
    }

    public static void registerType(Class<?> intf, ObjectFactory impl, EmbeddedObjectFactory modifier) {
        var reg = registry.get(intf);
        if (isNull(reg) || isNull(reg.getImplClass())) {
            forceRegisterType(intf, impl, modifier);
        }
    }

    public static void forceRegisterType(Class<?> intf, ObjectFactory impl, EmbeddedObjectFactory modifier) {
        registry.put(intf, RegistryEntry.builder().implFactory(impl).orgImplFactory(impl).modifierFactory(modifier).orgModifierFactory(modifier).build());
    }

    @SuppressWarnings("unchecked")
    public static <T> void envelopType(Class<T> intf, EnvelopFactory<T> impl, EmbeddedEnvelopFactory modifier) {
        var reg = registry.get(intf);
        var implFactory = reg.getImplFactory();

        reg.setImplFactory(() -> impl.envelop(implFactory));
        if (nonNull(reg.getModifierFactory())) {
            var embeddedFactory = reg.getModifierFactory();
            if (nonNull(embeddedFactory) && nonNull(modifier)) {
                reg.setModifierFactory((parent, value) -> modifier.envelop(embeddedFactory, parent, value));
            }
        }
    }

    public static void cleanEnvelopedType(Class<?> intf) {
        var reg = registry.get(intf);

        reg.setImplFactory(reg.orgImplFactory);
        reg.setModifierFactory(reg.orgModifierFactory);
    }

    public static void cleanAllEnvelopedTypes() {
        registry.forEach((c, reg) -> {
            reg.setImplFactory(reg.orgImplFactory);
            reg.setModifierFactory(reg.orgModifierFactory);
        });
    }

    public static void envelopFactory(EnvelopingObjectFactory factory) {
        envelopingFactory = factory;
    }

    public static void clearEnvelopingFactory() {
        envelopingFactory = null;
    }

    public static ObjectFactory singleton(Object object) {
        return () -> object;
    }

    public static void setProjectionProvider(ProjectionProvider provider) {
        projections = provider;
    }

    public static ProjectionProvider getProjectionProvider() {
        return projections;
    }

    public static void debug() {
        log.info("Registered classes: ");
        registry.forEach((key, value) -> log.info("- {}: {}", key, value));
    }

    private static ProjectionProvider initProjectionProvider() {
        try {
            var cls = Class.forName("net.binis.codegen.projection.provider.CodeGenProjectionProvider");
            return (ProjectionProvider) cls.getDeclaredConstructors()[0].newInstance();
        } catch (Exception e) {
            //Do nothing
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T projection(Object object, Class<T> projection) {
        if (nonNull(object)) {
            if (nonNull(projections)) {

//                if (object instanceof Map || object instanceof List || object instanceof Set) {
//                    return projections.handleCollection(object);
//                }

                return (T) projectionsCache.computeIfAbsent(projection, k ->
                                new HashMap<>())
                        .computeIfAbsent(object.getClass(), k ->
                                projections.create(k, projection)).create(object);
            } else {
                if (projection.isInstance(object)) {
                    return projection.cast(object);
                } else {
                    throw new GenericCodeGenException("Projections provider not present!");
                }
            }
        } else {
            return null;
        }
    }

    public static <T> T cast(Object object, Class<T> projection) {
        if (projection.isInstance(object)) {
            return projection.cast(object);
        }
        return projection(object, projection);
    }

    @SuppressWarnings("unchecked")
    private static <T> T defaultCreate(Class<?> impl, Class<T> cls) {
        var ann = cls.getDeclaredAnnotation(Default.class);
        if (nonNull(ann)) {
            return (T) create(impl, ann.value());
        }
        return internalEnvelop(null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T internalCreate(Class<T> cls) {
        var entry = registry.get(cls);
        if (entry != null) {
            return (T) entry.getImplFactory().create();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T internalEnvelop(T instance) {
        if (nonNull(envelopingFactory)) {
            return (T) envelopingFactory.envelop(instance);
        }
        return instance;
    }

    public interface IdDescription {
        String getName();

        Class<?> getType();
    }

    @Data
    @Builder
    private static class RegistryEntry {
        private Class<?> implClass;
        private ObjectFactory implFactory;
        private ObjectFactory orgImplFactory;
        private EmbeddedObjectFactory modifierFactory;
        private EmbeddedObjectFactory orgModifierFactory;
    }

    @Data
    @Builder
    private static class IdRegistryEntry implements IdDescription {
        private String name;
        private Class<?> type;
    }

}
