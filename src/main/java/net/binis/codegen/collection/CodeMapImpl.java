package net.binis.codegen.collection;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2026 Binis Belev
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

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.nonNull;

public class CodeMapImpl<K, V, R> implements CodeMap<K, V, R> {

    protected final R parent;
    protected final Map<K, V> map;
    protected final Consumer<K> keyValidator;
    protected final Consumer<V> valueValidator;

    public CodeMapImpl(R parent, Map<K, V> map) {
        this.parent = parent;
        this.map = map;
        this.keyValidator = null;
        this.valueValidator = null;
    }

    public CodeMapImpl(R parent, Map<K, V> map, Consumer<K> keyValidator, Consumer<V> valueValidator) {
        this.parent = parent;
        this.map = map;
        this.keyValidator = keyValidator;
        this.valueValidator = valueValidator;
    }

    @Override
    public CodeMap<K, V, R> put(K key, V value) {
        validate(key, value);
        map.put(key, value);
        return this;
    }

    @Override
    public CodeMap<K, V, R> putAll(Map<? extends K, ? extends V> map) {
        map.forEach(this::put);
        return this;
    }

    @Override
    public CodeMap<K, V, R> remove(Object key) {
        map.remove(key);
        return this;
    }

    @Override
    public CodeMap<K, V, R> clear() {
        map.clear();
        return this;
    }

    @Override
    public CodeMap<K, V, R> replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        map.replaceAll((k, v) -> {
            validate(k, v);
            return function.apply(k, v);
        });
        return this;
    }

    @Override
    public CodeMap<K, V, R> putIfAbsent(K key, V value) {
        validate(key, value);
        map.putIfAbsent(key, value);
        return this;
    }

    @Override
    public CodeMap<K, V, R> remove(Object key, Object value) {
        map.remove(key, value);
        return this;
    }

    @Override
    public CodeMap<K, V, R> replace(K key, V oldValue, V newValue) {
        validate(key, newValue);
        map.replace(key, oldValue, newValue);
        return this;
    }

    @Override
    public CodeMap<K, V, R> replace(K key, V value) {
        validate(key, value);
        map.replace(key, value);
        return this;
    }

    @Override
    public CodeMap<K, V, R> computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        validateKey(key);
        map.computeIfAbsent(key, k -> {
            var value = mappingFunction.apply(k);
            validateValue(value);
            return value;
        });
        return this;
    }

    @Override
    public CodeMap<K, V, R> computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        validateKey(key);
        map.computeIfPresent(key, (k, v) -> {
            var value = remappingFunction.apply(k, v);
            validateValue(value);
            return value;
        });
        return this;
    }

    @Override
    public CodeMap<K, V, R> compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        validateKey(key);
        map.compute(key, (k, v) -> {
            var value = remappingFunction.apply(k, v);
            validateValue(value);
            return value;
        });
        return this;
    }

    @Override
    public CodeMap<K, V, R> merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        validateKey(key);
        map.merge(key, value, (old, v) -> {
            var val = remappingFunction.apply(old, v);
            validateValue(val);
            return val;
        });
        return this;
    }

    @Override
    public R done() {
        return parent;
    }

    protected void validate(K key, V value) {
        validateKey(key);
        validateValue(value);
    }

    protected void validateKey(K key) {
        if (nonNull(keyValidator)) {
            keyValidator.accept(key);
        }
    }

    protected void validateValue(V value) {
        if (nonNull(valueValidator)) {
            valueValidator.accept(value);
        }
    }

    @Override
    public CodeMap<K, V, R> _if(boolean condition, Consumer<CodeMap<K, V, R>> consumer) {
        if (condition) {
            consumer.accept(this);
        }
        return this;
    }

    @Override
    public CodeMap<K, V, R> _if(boolean condition, BiConsumer<CodeMap<K, V, R>, R> consumer) {
        if (condition) {
            consumer.accept(this, parent);
        }
        return this;
    }

    @Override
    public CodeMap<K, V, R> _if(boolean condition, Consumer<CodeMap<K, V, R>> consumer, Consumer<CodeMap<K, V, R>> elseConsumer) {
        if (condition) {
            consumer.accept(this);
        } else {
            elseConsumer.accept(this);
        }
        return this;
    }

    @Override
    public CodeMap<K, V, R> _if(boolean condition, BiConsumer<CodeMap<K, V, R>, R> consumer, BiConsumer<CodeMap<K, V, R>, R> elseConsumer) {
        if (condition) {
            consumer.accept(this, parent);
        } else {
            elseConsumer.accept(this, parent);
        }
        return this;
    }

    @Override
    public CodeMap<K, V, R> _self(BiConsumer<CodeMap<K, V, R>, R> consumer) {
        consumer.accept(this, parent);
        return this;
    }

    @Override
    public CodeMap<K, V, R> _map(Object source) {
        throw new UnsupportedOperationException("Not implemented yet!");
        //TODO: Implement handling for collections.
    }

}
