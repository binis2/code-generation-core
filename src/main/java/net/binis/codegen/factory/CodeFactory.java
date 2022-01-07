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

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.binis.codegen.tools.Reflection.initialize;

@Slf4j
public class CodeFactory {

    private static final Map<Class<?>, RegistryEntry> registry = new HashMap<>();
    private static final Map<Class<?>, IdRegistryEntry> idRegistry = new HashMap<>();
    private static EnvelopingObjectFactory envelopingFactory;

    private CodeFactory() {
        //Do nothing
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> cls) {
        var entry = registry.get(cls);
        if (entry != null) {
            return internalEnvelop((T) entry.getImplFactory().create());
        } else {
            var parent = cls.getDeclaringClass();
            if (nonNull(parent)) {
                return (T) defaultCreate(cls, parent);
            } else {
                return defaultCreate(cls, cls);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> cls, String defaultClass) {
        var entry = registry.get(cls);
        Object obj = null;
        if (entry != null) {
            obj = entry.getImplFactory().create();
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
    public static <M, T, P> M modify(P parent, T value) {
        var entry = registry.get(value.getClass());
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
        if (entry != null) {
            if (entry.getImplClass() == null) {
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
        if (!registry.containsKey(intf)) {
            registry.put(intf, RegistryEntry.builder().implFactory(impl).modifierFactory(modifier).orgModifierFactory(modifier).build());
        }
    }

    public static void forceRegisterType(Class<?> intf, ObjectFactory impl, EmbeddedObjectFactory modifier) {
        registry.put(intf, RegistryEntry.builder().implFactory(impl).modifierFactory(modifier).orgModifierFactory(modifier).build());
    }

    @SuppressWarnings("unchecked")
    public static void envelopType(Class<?> intf, EnvelopFactory impl, EmbeddedEnvelopFactory modifier) {
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

    public static void clearEnvelopedType(Class<?> intf, EnvelopFactory impl, EmbeddedEnvelopFactory modifier) {
        var reg = registry.get(intf);
        var implFactory = reg.getImplFactory();

        reg.setImplFactory(() -> impl.envelop(implFactory));
        if (nonNull(reg.getModifierFactory())) {
            var embeddedFactory = reg.getModifierFactory();
            if (nonNull(embeddedFactory) && nonNull(modifier)) {
                reg.setModifierFactory(reg.orgModifierFactory);
            }
        }
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
