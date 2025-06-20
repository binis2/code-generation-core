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

import net.binis.codegen.async.Async;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsyncTest {

    @Test
    void test() throws ExecutionException, InterruptedException {
        var f = Async.start().collect(() -> 5);
        var r = f.get();
        assertEquals(5, r);
    }

    @Test
    void testVirtual() throws ExecutionException, InterruptedException {
        var f = Async.virtual().collect(() -> 5);
        var r = f.get();
        assertEquals(5, r);
    }
}
