package net.binis.codegen.objects;

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

import net.binis.codegen.annotation.Default;
import net.binis.codegen.factory.CodeFactory;

@Default("net.binis.codegen.objects.impl.PairImpl")
public interface Pair<K, V> {

    K getKey();
    K getLeft();
    V getValue();
    V getRight();
    Pair<K, V> key(K key);
    Pair<K, V> left(K left);
    Pair<K, V>  value(V value);
    Pair<K, V>  right(V right);

    @SuppressWarnings("unchecked")
    static <K1, V1> Pair<K1, V1> of(K1 key, V1 value) {
        return CodeFactory.create(Pair.class).key(key).value(value);
    }

    @SuppressWarnings("unchecked")
    static <K1, V1> Pair<K1, V1> create() {
        return CodeFactory.create(Pair.class);
    }

}
