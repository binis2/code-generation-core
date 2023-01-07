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

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.binis.codegen.map.Mapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    @Test
    void testSame() {
        var test = new TestMap();
        test.setBaseString("base");
        test.setString1("test");
        test.setLong1(1L);
        test.setInt1(2);
        var resultTest = Mapper.map(test, TestMap.class);
        assertNotSame(test, resultTest);
        assertEquals(test.getBaseString(), resultTest.getBaseString());
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals(test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), resultTest.getInt1());
    }

    @Test
    void testDifferent() {
        var test = new TestMap();
        test.setBaseString("base");
        test.setString1("test");
        test.setLong1(1L);
        test.setInt1(2);
        var resultTest = Mapper.map(test, TestMap2.class);
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals((long) test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), (int) resultTest.getInt1());
        assertNull(test.getInt2());
        assertEquals(0, resultTest.getInt2());
    }

    @Data
    private static class BaseMap {
        private String baseString;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class TestMap extends BaseMap {
        private String string1;
        private Long long1;
        private int int1;
        private Integer int2;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class TestMap2 extends BaseMap {
        private String string1;
        private long long1;
        private Integer int1;
        private int int2;
    }

}
