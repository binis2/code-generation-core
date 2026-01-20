package net.binis.codegen.objects.impl;

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

import net.binis.codegen.factory.CodeFactory;
import net.binis.codegen.objects.Pair;

import java.io.Serializable;
import java.util.Objects;

public class PairImpl<K, V> implements Pair<K, V>, Serializable {

    static {
        CodeFactory.registerType(Pair.class, PairImpl::new, null);
    }

    protected K key;
    protected V value;

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public K getLeft() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V getRight() {
        return value;
    }

    @Override
    public Pair<K, V> key(K key) {
        this.key = key;
        return this;
    }

    @Override
    public Pair<K, V> left(K left) {
        this.key = left;
        return this;
    }

    @Override
    public Pair<K, V> value(V value) {
        this.value = value;
        return this;
    }

    @Override
    public Pair<K, V> right(V right) {
        this.value = right;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var pair = (PairImpl<?, ?>) o;

        return Objects.equals(key, pair.key) &&
                Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

}
