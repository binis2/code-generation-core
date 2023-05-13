package net.binis.codegen.factory;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2022 Binis Belev
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

import net.binis.codegen.tools.Holder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static net.binis.codegen.tools.Functional.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionalTest {

    @Test
    void doWhileTest() {
        Holder<Integer> test = Holder.of(0);
        _do(() -> test.update(test.get() + 1))
                ._while(i -> i < 10);
        assertEquals(10, test.get());
    }

    @Test
    void doWhileThenTest() {
        Holder<Integer> test = Holder.of(0);
        _do(() -> test.update(test.get() + 1))
                ._while(i -> i < 10)
                ._then(i -> assertEquals(10, i));
    }

    @Test
    void recursive() {
        _recursive(10)
                ._init(ArrayList::new)
                ._on(this::positive)
                ._perform((i, list) -> list.add(i))
                ._then(list -> assertEquals(10, list.size()));
    }

    @Test
    void recursiveGet() {
        _recursive(10)
                ._init(ArrayList::new)
                ._on(this::positive)
                ._perform((i, list) -> list.add(i))
                ._get().ifPresentOrElse(list ->
                        assertEquals(10, list.size()), Assertions::fail);
    }

    @Test
    void recursivePerform() {
        _recursive(10)
                ._on(this::positive)
                ._perform(i -> assertTrue(i > 0));
    }

    @Test
    void recursiveDo() {
        _recursive(10)
                ._do(this::positive)
                ._then(i -> assertTrue(i > 0));
    }

    @Test
    void forTest() {
        var result = _for(List.of("1", "2", "3"), "0")._do((v, r) -> r + v);
        assertEquals("0123", result);
    }

    protected Integer positive(Integer i) {
        i--;
        if (i > 0) {
            return i;
        } else {
            return null;
        }
    }

}
