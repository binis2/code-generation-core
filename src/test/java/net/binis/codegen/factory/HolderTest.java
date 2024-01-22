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

import net.binis.codegen.tools.Holder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HolderTest {

    @Test
    void test() {
        var holder = Holder.of("asd");
        assertEquals("asd", holder.get());

        holder.set("qwe");
        assertEquals("qwe", holder.get());

        holder.update("zxc");
        assertEquals("zxc", holder.get());

        assertFalse(holder.isEmpty());
        assertTrue(holder.isPresent());

        assertTrue(Holder.blank().isEmpty());

        assertEquals("asd", Holder.lazy(() -> "asd").get());
    }

}
