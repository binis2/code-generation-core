package net.binis.codegen.collection;

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

import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class CodeMapImpl<K, V, R> implements CodeMap<K, V, R> {

    private final R parent;
    private final Map<K, V> map;
    private final Consumer<K> keyValidator;
    private final Consumer<V> valueValidator;

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
    public R and() {
        return parent;
    }

    protected void validate(K key, V value) {
        if(nonNull(keyValidator)) {
            keyValidator.accept(key);
        }
        if(nonNull(valueValidator)) {
            valueValidator.accept(value);
        }

    }
}
