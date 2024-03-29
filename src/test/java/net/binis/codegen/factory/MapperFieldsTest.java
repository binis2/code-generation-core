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

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.annotation.type.GenerationStrategy;
import net.binis.codegen.map.Mapper;
import net.binis.codegen.map.MapperFactory;
import net.binis.codegen.map.MappingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.util.Map;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MapperFieldsTest {

    @BeforeEach
    void setup() {
        CodeFactory.create(MapperFactory.class).clearMapping(TestMap.class, TestMap2.class);
    }

    @Test
    void testSame() {
        var test = new TestMap();
        test.setBaseString("base");
        test.setString1("test");
        test.setLong1(1L);
        test.setInt1(2);
        var resultTest = Mapper.map(test, TestMap.class, MappingStrategy.FIELDS);
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
        test.setConvert1(3);
        test.setConvert2(true);
        var resultTest = Mapper.map(test, TestMap2.class, MappingStrategy.FIELDS);
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals((long) test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), (int) resultTest.getInt1());
        assertNull(test.getInt2());
        assertEquals(0, resultTest.getInt2());
        assertEquals(3L, resultTest.getConvert1());
        assertEquals("true", resultTest.getConvert2());
    }

    @Test
    void testBuilder() {
        var test = new TestMap();
        test.setBaseString("base");
        test.setString1("test");
        test.setLong1(1L);
        test.setInt1(2);
        Mapper.map().strategy(MappingStrategy.FIELDS).source(TestMap.class).destination(TestMap2.class).register();
        var resultTest = Mapper.map(test, TestMap2.class);
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals((long) test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), (int) resultTest.getInt1());
        assertNull(test.getInt2());
        assertEquals(0, resultTest.getInt2());
    }

    @Test
    void testCustomBuilder() {
        var test = new TestMap();
        test.setBaseString("base");
        test.setString1("test");
        test.setLong1(1L);
        test.setInt1(2);
        test.setConvert1(3);
        test.setConvert2(true);
        Mapper.map().strategy(MappingStrategy.FIELDS).source(TestMap.class).destination(TestMap2.class).custom((s, d) ->
                d.setBuilder(s.getConvert1() + s.getString1()));
        var resultTest = Mapper.map(test, TestMap2.class);
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals((long) test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), (int) resultTest.getInt1());
        assertNull(test.getInt2());
        assertEquals(0, resultTest.getInt2());
        assertEquals(3L, resultTest.getConvert1());
        assertEquals("true", resultTest.getConvert2());
        assertEquals("3test", resultTest.getBuilder());
    }

    @Test
    void testCreation() {
        TestMap.instanceCount = 0;
        var result = Mapper.map(TestMap2.builder().int2(1).build(), new TestMap(), MappingStrategy.FIELDS);
        assertNotNull(result);
        assertEquals(1, result.getInt2());
        assertEquals(1, TestMap.instanceCount);
    }

    @Test
    void testCreationClass() {
        TestMap.instanceCount = 0;
        var result = Mapper.map(TestMap2.builder().int2(1).build(), TestMap.class, MappingStrategy.FIELDS);
        assertNotNull(result);
        assertEquals(1, result.getInt2());
        assertEquals(1, TestMap.instanceCount);
    }

    @Data
    private static class BaseMap {
        private String baseString;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class TestMap extends BaseMap {

        private static int instanceCount = 0;

        private TestMap() {
            instanceCount++;
        }

        private String string1;
        private Long long1;
        private int int1;
        private Integer int2;

        private int convert1;
        private boolean convert2;

        private TestMap2 map;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    private static class TestMap2 extends BaseMap {
        private String string1;
        private long long1;
        private Integer int1;
        private int int2;

        private long convert1;
        private String convert2;

        private String builder;
    }

}
