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

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static net.binis.codegen.tools.Reflection.initialize;

@Slf4j
public class CodeFactory {

    private static final Map<Class<?>, RegistryEntry> registry = new HashMap<>();

    private CodeFactory() {
        //Do nothing
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> cls) {
        var entry = registry.get(cls);
        if (entry != null) {
            return (T) entry.getImplFactory().create();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> cls, String defaultClass) {
        var entry = registry.get(cls);
        if (entry != null) {
            return (T) entry.getImplFactory().create();
        } else {
            try {
                initialize(defaultClass);
                return create(cls);
            } catch (Exception e) {
                log.error("Can't find class: {}", defaultClass);
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <M, T, P> M modify(P parent, T value) {
        var entry = registry.get(value.getClass());
        if (entry != null) {
            return (M) entry.getModifierFactory().create(parent, value);
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

    public static void registerType(Class<?> intf, ObjectFactory impl, EmbeddedObjectFactory modifier) {
        if (!registry.containsKey(intf)) {
            registry.put(intf, RegistryEntry.builder().implFactory(impl).modifierFactory(modifier).build());
        }
    }

    public static void forceRegisterType(Class<?> intf, ObjectFactory impl, EmbeddedObjectFactory modifier) {
        registry.put(intf, RegistryEntry.builder().implFactory(impl).modifierFactory(modifier).build());
    }


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

    public static ObjectFactory singleton(Object object) {
        return () -> object;
    }

    @Data
    @Builder
    private static class RegistryEntry {
        private Class<?> implClass;
        private ObjectFactory implFactory;
        private EmbeddedObjectFactory modifierFactory;
    }
}
