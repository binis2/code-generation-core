package net.binis.codegen.collection;

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

import java.util.Map;

public class CodeMapImpl<K, V, R> implements CodeMap<K, V, R> {

    private final R parent;
    private final Map<K, V> map;

    public CodeMapImpl(R parent, Map<K, V> map) {
        this.parent = parent;
        this.map = map;
    }

    @Override
    public CodeMap<K, V, R> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    @Override
    public R and() {
        return parent;
    }
}
