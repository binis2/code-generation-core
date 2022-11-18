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


import lombok.Data;
import net.binis.codegen.annotation.Default;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeFactoryTest {

    @Test
    void testCreation() {
        var inst = CodeFactory.create(TestIntf.class, "test");

        assertEquals("test", inst.getParam());
    }

    @Default("net.binis.codegen.factory.CodeFactoryTest$TestImpl")
    public interface TestIntf {
        String getParam();
    }

    @Data
    public static class TestImpl implements TestIntf {
        private final String param;

        {
            CodeFactory.registerType(TestIntf.class, params -> new TestImpl((String) params[0]), null);
        }

        protected TestImpl(String param) {
            this.param = param;
        }

    }

}
