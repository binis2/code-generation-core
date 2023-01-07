package net.binis.codegen.factory;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2023 Binis Belev
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

import org.junit.jupiter.api.Test;

import static net.binis.codegen.tools.Reflection.isGetter;
import static net.binis.codegen.tools.Reflection.isSetter;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReflectionTest {

    @Test
    void testGettersSetters() throws NoSuchMethodException {
        assertTrue(isGetter(TestInterface.class.getDeclaredMethod("isWorking")));
        assertTrue(isGetter(TestInterface.class.getDeclaredMethod("getWorking")));
        assertTrue(isSetter(TestInterface.class.getDeclaredMethod("setWorking", String .class)));

        assertFalse(isGetter(TestInterface.class.getDeclaredMethod("getNotWorking1")));
        assertFalse(isGetter(TestInterface.class.getDeclaredMethod("getNotWorking2", String.class)));
        assertFalse(isSetter(TestInterface.class.getDeclaredMethod("setNotWorking3")));
        assertFalse(isSetter(TestInterface.class.getDeclaredMethod("setNotWorking4", String.class)));
        assertFalse(isGetter(TestInterface.class.getDeclaredMethod("gettNotWorking5")));
        assertFalse(isSetter(TestInterface.class.getDeclaredMethod("settNotWorking6", String.class)));
    }

    private interface TestInterface {

        void getNotWorking1();
        String getNotWorking2(String param);
        void setNotWorking3();
        String setNotWorking4(String value);
        String gettNotWorking5();
        void settNotWorking6(String value);

        boolean isWorking();
        String getWorking();
        void setWorking(String value);

    }

}
