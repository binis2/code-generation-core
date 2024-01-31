package net.binis.codegen.factory;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2024 Binis Belev
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.annotation.Default;
import net.binis.codegen.discovery.Discoverer;
import net.binis.codegen.exception.CodeFactoryExeception;
import net.binis.codegen.exception.GenericCodeGenException;
import net.binis.codegen.objects.Pair;
import net.binis.codegen.objects.base.enumeration.CodeEnum;
import net.binis.codegen.objects.base.enumeration.CodeEnumImpl;
import net.binis.codegen.tools.Holder;
import net.binis.codegen.tools.Reflection;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.binis.codegen.tools.Reflection.*;

@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class CodeFactory {

    protected static final Map<Class<?>, RegistryEntry> registry = new HashMap<>();
    protected static final Map<Class<?>, IdRegistryEntry> idRegistry = new HashMap<>();
    protected static EnvelopingObjectFactory envelopingFactory;
    protected static final Set<Class<?>> customProxyClassesRegistry = new HashSet<>();
    protected static final List<Pair<Class<?>, ProjectionProvider>> customProxyClasses = new ArrayList<>();
    protected static final Map<Class<?>, Map<Class<?>, ProjectionInstantiation>> projectionsCache = new HashMap<>();
    protected static final List<ForeignObjectFactory> foreignFactories = new ArrayList<>();
    protected static ProjectionProvider projections = initProjectionProvider();
    protected static ProxyProvider proxies = initProxyProvider();

    protected static final Map<Class<?>, EnumEntry> enumRegistry = new HashMap<>();

    protected CodeFactory() {
        //Do nothing
    }

    static {
        registerType(Object.class, Object::new);
        Discoverer.findAnnotations().stream().filter(Discoverer.DiscoveredService::isConfig).forEach(config -> {
            var method = Reflection.findMethod("initialize", config.getCls());
            if (nonNull(method) && Modifier.isStatic(method.getModifiers())) {
                Reflection.invokeStatic(method);
            }
        });
    }

    @SneakyThrows
    public static <T> T create(Class<T> cls, Object... params) {
        var entry = registry.get(cls);
        if (entry != null) {
            if (nonNull(entry.getImplFactory())) {
                try {
                    return internalEnvelop((T) entry.getImplFactory().create(params));
                } catch (CodeFactoryExeception e) {
                    throw e.getCause();
                }
            } else {
                return null;
            }
        } else {
            var result = createWithFactories(cls, params);
            if (nonNull(result)) {
                return result;
            }
            result = defaultCreate(cls, cls, params);
            if (isNull(result)) {
                var parent = cls.getDeclaringClass();
                if (nonNull(parent)) {
                    result = (T) defaultCreate(cls, parent, params);
                }
            }
            if (isNull(result)) {
                if (!cls.isInterface()) {
                    try {
                        var ctor = findConstructor(cls, params);
                        result = (T) ctor.newInstance(params);
                        var ctorMap = new HashMap<Integer, Constructor>();
                        ctorMap.put(params.length, ctor);

                        registerType(cls, p -> {
                            try {
                                return ctorMap.computeIfAbsent(p.length, k -> findConstructor(cls, p)).newInstance(p);
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                return null;
                            }
                        }, null);
                    } catch (Exception e) {
                        registerType(cls, (ObjectFactory) null, null);
                    }
                } else {
                    registerType(cls, (ObjectFactory) null, null);
                }
            }
            return result;
        }
    }

    protected static <T> T createWithFactories(Class<T> cls, Object[] params) {
        for (var factory : foreignFactories) {
            try {
                var result = factory.create(cls, params);
                if (nonNull(result)) {
                    registerType(cls, par -> factory.create(cls, par));
                    return (T) result;
                }
            } catch (Exception e) {
                //Do nothing
            }
        }
        return null;
    }

    @SneakyThrows
    public static <T> T createDefault(Class<T> cls, String defaultClass, Object... params) {
        var entry = registry.get(cls);
        Object obj;
        if (entry != null) {
            if (entry.getImplFactory() != null) {
                obj = entry.getImplFactory().create(params);
            } else {
                return null;
            }
        } else {
            obj = createWithFactories(cls, params);
            if (isNull(obj)) {
                try {
                    obj = internalCreate(cls, initialize(defaultClass, params), params);
                } catch (Exception e) {
                    if (e instanceof InvocationTargetException ex && ex.getTargetException() instanceof CodeFactoryExeception cfe) {
                        throw cfe.getCause();
                    }

                    log.error("Can't instantiate class: {}", defaultClass);
                }
            } else {
                return (T) obj;
            }
        }

        return internalEnvelop((T) obj);
    }

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

    public static boolean isRegisteredType(Class<?> intf) {
        return registry.containsKey(intf);
    }

    public static Class<?> lookup(Class<?> intf) {
        var entry = registry.get(intf);

        if (isNull(entry)) {
            create(intf);
            entry = registry.get(intf);
        }

        if (entry != null) {
            if (entry.getImplClass() == null && entry.getImplFactory() != null) {
                var inst = entry.getImplFactory().create();
                if (nonNull(inst)) {
                    entry.setImplClass(inst.getClass());
                }
            }
            return entry.getImplClass();
        }
        return null;
    }

    public static IdDescription lookupId(Class<?> intf) {
        return idRegistry.get(intf);
    }

    public static void registerForeignFactory(ForeignObjectFactory factory) {
        foreignFactories.add(factory);
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

    public static void registerType(Class<?> intf, ObjectFactory impl) {
        registerType(intf, impl, null);
    }

    public static void registerType(Class<?> intf, Supplier impl) {
        registerType(intf, params -> impl.get(), null);
    }

    public static void registerType(Class<?> intf, Supplier impl, EmbeddedObjectFactory modifier) {
        registerType(intf, params -> impl.get(), modifier);
    }

    public static void forceRegisterType(Class<?> intf, ObjectFactory impl, EmbeddedObjectFactory modifier) {
        registry.put(intf, RegistryEntry.builder().implFactory(impl).orgImplFactory(impl).modifierFactory(modifier).orgModifierFactory(modifier).build());
    }

    public static boolean unregisterType(Class<?> cls) {
        return nonNull(registry.remove(cls));
    }

    public static <T> void envelopType(Class<T> intf, EnvelopFactory<T> impl, EmbeddedEnvelopFactory modifier) {
        var reg = registry.get(intf);
        var implFactory = reg.getImplFactory();

        reg.setImplFactory((params) -> impl.envelop(implFactory));
        if (nonNull(reg.getModifierFactory()) && nonNull(modifier)) {
            reg.setModifierFactory((parent, value, params) -> modifier.envelop(reg.getModifierFactory(), parent, value, params));
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
        return params -> object;
    }

    public static ObjectFactory lazy(Supplier supplier) {
        var inst = Holder.lazy(supplier);
        return params -> inst.get();
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

    public static <T> T projection(Object object, Class<T> projection) {
        if (nonNull(object)) {
            if (nonNull(projections)) {
                return (T) projectionsCache.computeIfAbsent(projection, k ->
                                new ConcurrentHashMap<>())
                        .computeIfAbsent(object.getClass(), k ->
                                checkForCustomClass(k).orElse(projections)
                                        .create(k, projection))
                        .create(object);
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

    public static <T> T proxy(Class<T> cls, InvocationHandler handler) {
        return (T) proxies.proxy(cls, handler);
    }

    public static Object projections(Object object, Class<?>... projections) {
        if (nonNull(object) && projections.length > 0) {
            return projection(object, projections[0]);
        } else {
            return object;
        }
    }

    public static <T> T cast(Object object, Class<T> projection) {
        if (projection.isInstance(object)) {
            return projection.cast(object);
        }
        return projection(object, projection);
    }

    public static void registerCustomProxyClass(Class<?> cls, ProjectionProvider provider) {
        customProxyClasses.add(Pair.of(cls, provider));
        customProxyClassesRegistry.add(cls);
    }

    public static boolean isCustomProxyClass(Class<?> cls) {
        return customProxyClassesRegistry.contains(cls);
    }

    public static <T extends CodeEnum> T initializeEnumValue(Class<T> cls, String name, int ordinal, Object... params) {
        if (CodeEnum.class.isAssignableFrom(cls)) {
            var registry = enumRegistry.computeIfAbsent(cls, c -> EnumEntry.builder().initializer(buildEnumInitializer(cls)).build());
            CodeEnum inst;
            if (ordinal == Integer.MIN_VALUE) {
                ordinal = generateUniqueOrdinal(registry);
            } else {
                inst = registry.ordinals.get(ordinal);
                if (nonNull(inst)) {
                    return (T) inst;
                }
                inst = registry.values.get(name);
                if (nonNull(inst)) {
                    return (T) inst;
                }
            }
            inst = registry.initializer.initialize(ordinal, name, params);

            registry.ordinals.put(ordinal, inst);
            registry.values.put(name, inst);

            return (T) inst;
        }
        throw new GenericCodeGenException("Class " + cls.getCanonicalName() + " isn't enumeration class!");
    }

    protected static int generateUniqueOrdinal(EnumEntry registry) {
        var result = -100;
        while (nonNull(registry.ordinals.get(result))) {
            result--;
        }
        return result;
    }

    public static <T extends CodeEnum> T initializeUnknownEnumValue(Class<T> cls, String name, int ordinal, Object... params) {
        var result = initializeEnumValue(cls, name, ordinal, params);
        ((CodeEnumImpl) result).setUnknown();
        return result;
    }


    public static <T extends CodeEnum> T enumValueOf(Class<T> cls, String name) {
        var registry = enumRegistry.get(cls);
        if (nonNull(registry)) {
            return (T) registry.values.get(name);
        }
        return null;
    }

    public static <T extends CodeEnum> T enumValueOf(Class<T> cls, int ordinal) {
        var registry = enumRegistry.get(cls);
        if (nonNull(registry)) {
            return (T) registry.ordinals.get(ordinal);
        }
        return null;
    }

    public static <T extends CodeEnum> Map<Integer, T> enumValuesMap(Class<T> cls) {
        var registry = enumRegistry.get(cls);
        if (nonNull(registry)) {
            return (Map) Collections.unmodifiableMap(registry.ordinals);
        }
        return Collections.emptyMap();
    }

    public static <T extends CodeEnum> T[] enumValues(Class<T> cls) {
        var registry = enumRegistry.get(cls);
        if (nonNull(registry)) {
            var list = registry.ordinals.values().stream().sorted(Comparator.comparing(CodeEnum::ordinal)).toList();
            if (!list.isEmpty()) {
                var arr = (T[]) Array.newInstance(cls, list.size());
                for (var i = 0; i < list.size(); i++) {
                    arr[i] = (T) list.get(i);
                }
                return arr;
            }
        }
        return (T[]) Array.newInstance(cls, 0);
    }

    public static List<Class<? extends CodeEnum>> registeredEnums() {
        return (List) enumRegistry.keySet().stream().toList();
    }

    public static CodeFactoryExeception exception(String message, Object... params) {
        return new CodeFactoryExeception(String.format(message, params));
    }

    public static CodeFactoryExeception exception(String message, Throwable cause, Object... params) {
        return new CodeFactoryExeception(String.format(message, params), cause);
    }

    public static CodeFactoryExeception exception(Throwable cause) {
        return new CodeFactoryExeception(cause);
    }

    protected static <T extends CodeEnum> EnumInitializer buildEnumInitializer(Class<T> cls) {
        var a = cls.getAnnotation(Default.class);
        if (isNull(a)) {
            throw new GenericCodeGenException("Can't find implementation pointer for " + cls.getCanonicalName());
        }
        try {
            var c = loadClass(a.value());
            var constructor = c.getDeclaredConstructors()[0];
            return ((ordinal, name, params) -> {
                var list = new ArrayList<>(params.length + 2);
                list.add(ordinal);
                list.add(name);
                Collections.addAll(list, params);
                if (constructor.getParameterCount() > list.size()) {
                    var types = constructor.getParameterTypes();
                    for (var i = list.size(); i < constructor.getParameterCount(); i++) {
                        list.add(defaultValue(types[i]));
                    }
                }
                try {
                    return (T) constructor.newInstance(list.toArray());
                } catch (Exception e) {
                    throw new GenericCodeGenException("Unable to initialize enum value!", e);
                }
            });
        } catch (Exception e) {
            throw new GenericCodeGenException("Can't find implementation class for " + cls.getCanonicalName());
        }
    }

    protected static Object defaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            if (byte.class.equals(type)) {
                return 0;
            }
            if (short.class.equals(type)) {
                return 0;
            }
            if (int.class.equals(type)) {
                return 0;
            }
            if (long.class.equals(type)) {
                return 0L;
            }
            if (float.class.equals(type)) {
                return 0.0f;
            }
            if (double.class.equals(type)) {
                return 0.0d;
            }
            if (char.class.equals(type)) {
                return '\u0000';
            }
            if (boolean.class.equals(type)) {
                return false;
            }
        }
        return null;
    }


    protected static ProjectionProvider initProjectionProvider() {
        try {
            var cls = Class.forName("net.binis.codegen.projection.provider.CodeGenProjectionProvider");
            return (ProjectionProvider) cls.getDeclaredConstructors()[0].newInstance();
        } catch (Exception e) {
            //Do nothing
        }
        return null;
    }

    protected static ProxyProvider initProxyProvider() {
        if (projections instanceof ProxyProvider) {
            return (ProxyProvider) projections;
        } else {
            return (cls, handler) -> Proxy.newProxyInstance(
                    CodeFactory.class.getClassLoader(),
                    new Class[] { cls },
                    handler);
        }
    }

    protected static Optional<ProjectionProvider> checkForCustomClass(Class<?> cls) {
        for (var c : customProxyClasses) {
            if (c.getKey().isAssignableFrom(cls)) {
                return Optional.of(c.getValue());
            }
        }
        return Optional.empty();
    }

    protected static <T> T defaultCreate(Class<?> impl, Class<T> cls, Object... params) {
        var ann = cls.getDeclaredAnnotation(Default.class);
        if (nonNull(ann)) {
            return (T) createDefault(impl, ann.value(), params);
        }
        return internalEnvelop(null);
    }

    protected static <T> T internalCreate(Class<T> cls, Class<?> impl, Object... params) {
        var entry = registry.get(cls);
        if (entry != null) {
            entry.setImplClass(impl);
            return (T) entry.getImplFactory().create(params);
        } else if (nonNull(impl)) {
            registerType(cls, par -> instantiate(impl, par));
            return (T) instantiate(impl, params);
        }
        return null;
    }

    protected static <T> T internalEnvelop(T instance) {
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
    protected static class RegistryEntry {
        protected Class<?> implClass;
        protected ObjectFactory implFactory;
        protected ObjectFactory orgImplFactory;
        protected EmbeddedObjectFactory modifierFactory;
        protected EmbeddedObjectFactory orgModifierFactory;
    }

    @Data
    @Builder
    protected static class IdRegistryEntry implements IdDescription {
        protected String name;
        protected Class<?> type;
    }

    @FunctionalInterface
    protected interface EnumInitializer {
        CodeEnum initialize(int ordinal, String name, Object... params);
    }

    @Data
    @Builder
    protected static class EnumEntry {
        @Builder.Default
        protected Map<Integer, CodeEnum> ordinals = new HashMap<>();
        @Builder.Default
        protected Map<String, CodeEnum> values = new HashMap<>();
        protected EnumInitializer initializer;
    }

}
