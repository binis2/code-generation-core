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
import java.time.temporal.*;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MapperTest {

    @BeforeEach
    void setup() {
        CodeFactory.create(MapperFactory.class).clearMapping(TestMap.class, TestMap2.class);
        CodeFactory.create(MapperFactory.class).clearMapping(BaseMap.class, BaseMap.class);
    }

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
        test.setConvert1(3);
        test.setConvert2(true);
        var resultTest = Mapper.map(test, TestMap2.class);
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals((long) test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), (int) resultTest.getInt1());
        assertNull(test.getInt2());
        assertEquals(0, resultTest.getInt2());
        assertEquals(3L, resultTest.getConvert1());
        assertEquals("true", resultTest.getConvert2());
    }

    @Test
    void testMapping() {
        assertEquals(1, Mapper.convert(1L, int.class));
        assertEquals(1.0, Mapper.convert(1L, double.class));
        assertEquals((byte) 1, Mapper.convert(1L, byte.class));
        assertEquals((short) 1, Mapper.convert(1L, short.class));
        assertEquals(true, Mapper.convert(1L, boolean.class));
        assertEquals((char) 1, Mapper.convert(1L, char.class));
        assertEquals(1, Mapper.convert(1L, float.class));
        assertEquals(1, Mapper.convert(1L, double.class));
    }

    @Test
    void testBuilder() {
        var test = new TestMap();
        test.setBaseString("base");
        test.setString1("test");
        test.setLong1(1L);
        test.setInt1(2);
        Mapper.map().source(TestMap.class).destination(TestMap2.class).register();
        var resultTest = Mapper.map(test, TestMap2.class);
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals((long) test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), (int) resultTest.getInt1());
        assertNull(test.getInt2());
        assertEquals(0, resultTest.getInt2());
    }

    @Test
    void testMappingInterface() {
        var test = new TestMap();
        test.setBaseString("base");
        test.setString1("test");
        test.setLong1(1L);
        test.setInt1(2);
        Mapper.map().source(BaseMap.class).destination(BaseMap.class).custom((in, out) -> out.setBaseString("changed"));
        var resultTest = Mapper.map(test, TestMap2.class);
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals((long) test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), (int) resultTest.getInt1());
        assertNull(test.getInt2());
        assertEquals(0, resultTest.getInt2());
        assertEquals("changed", resultTest.getBaseString());
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
        Mapper.map().source(TestMap.class).destination(TestMap2.class).custom((s, d) ->
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
    void testCustomBuilderKey() {
        var test = new TestMap();
        var key = this;
        test.setBaseString("base");
        test.setString1("test");
        test.setLong1(1L);
        test.setInt1(2);
        test.setConvert1(3);
        test.setConvert2(true);
        Mapper.map().source(TestMap.class).destination(TestMap2.class).custom((s, d) ->
                d.setBuilder(s.getConvert1() + s.getString1()));
        Mapper.map().key(key).source(TestMap.class).destination(TestMap2.class).register();

        var resultTest = Mapper.map(test, TestMap2.class);
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals((long) test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), (int) resultTest.getInt1());
        assertNull(test.getInt2());
        assertEquals(0, resultTest.getInt2());
        assertEquals(3L, resultTest.getConvert1());
        assertEquals("true", resultTest.getConvert2());
        assertEquals("3test", resultTest.getBuilder());

        resultTest = Mapper.map(test, TestMap2.class, key);
        assertEquals(test.getString1(), resultTest.getString1());
        assertEquals((long) test.getLong1(), resultTest.getLong1());
        assertEquals(test.getInt1(), (int) resultTest.getInt1());
        assertNull(test.getInt2());
        assertEquals(0, resultTest.getInt2());
        assertEquals(3L, resultTest.getConvert1());
        assertEquals("true", resultTest.getConvert2());
        assertNull(resultTest.getBuilder());
    }

    @Test
    void testKey() {
        var key = new Object();
        var test = new TestMap();
        test.setString1("test");

        Mapper.registerMapperKey(Object.class, String.class, key, (s, d) -> "test");

        assertEquals("test", Mapper.convert(test, String.class, key));
        assertEquals("MapperTest.TestMap(string1=test, long1=null, int1=0, int2=null, convert1=0, convert2=false, map=null)", Mapper.convert(test, String.class));
        assertEquals("test", Mapper.convert(test, String.class, key));
        assertEquals("MapperTest.TestMap(string1=test, long1=null, int1=0, int2=null, convert1=0, convert2=false, map=null)", Mapper.convert(test, String.class));
        assertEquals("test", Mapper.convert(test, String.class, key));
        assertEquals("MapperTest.TestMap(string1=test, long1=null, int1=0, int2=null, convert1=0, convert2=false, map=null)", Mapper.convert(test, String.class));
        assertEquals("test", Mapper.convert(test, String.class, key));
        assertEquals("MapperTest.TestMap(string1=test, long1=null, int1=0, int2=null, convert1=0, convert2=false, map=null)", Mapper.convert(test, String.class));
    }


    @Test
    void testEnum() {
        assertEquals(GenerationStrategy.PROTOTYPE, Mapper.convert("PROTOTYPE", GenerationStrategy.class));
        assertEquals(GenerationStrategy.IMPLEMENTATION, Mapper.convert("IMPLEMENTATION", GenerationStrategy.class));
    }

    @Test
    void testCreation() {
        TestMap.instanceCount = 0;
        var result = Mapper.map(TestMap2.builder().int2(1).build(), new TestMap());
        assertNotNull(result);
        assertEquals(1, result.getInt2());
        assertEquals(1, TestMap.instanceCount);
    }

    @Test
    void testCreationClass() {
        TestMap.instanceCount = 0;
        var result = Mapper.map(TestMap2.builder().int2(1).build(), TestMap.class);
        assertNotNull(result);
        assertEquals(1, result.getInt2());
        assertEquals(1, TestMap.instanceCount);
    }


    @Test
    void testDates() {
        CodeFactory.registerType(LocalDate.class, () -> LocalDate.now());
        CodeFactory.registerType(LocalDateTime.class, () -> LocalDateTime.now());
        CodeFactory.registerType(LocalTime.class, () -> LocalTime.now());
        CodeFactory.registerType(OffsetDateTime.class, () -> OffsetDateTime.now());
        CodeFactory.registerType(OffsetTime.class, () -> OffsetTime.now());
        CodeFactory.registerType(ZonedDateTime.class, () -> ZonedDateTime.now());
        Mapper.registerMapper(Temporal.class, Temporal.class, this::temporalConvert);

        assertEquals(LocalDate.of(2020, 1, 1), Mapper.convert(LocalDateTime.of(2020, 1, 1, 2, 15), LocalDate.class));
        assertEquals(LocalDateTime.of(2020, 1, 1, 0, 0), Mapper.convert(LocalDate.of(2020, 1, 1), LocalDateTime.class));
        assertEquals(LocalTime.of(2, 15), Mapper.convert(LocalDateTime.of(2020, 1, 1, 2, 15), LocalTime.class));
        assertEquals(OffsetTime.of(2, 15, 0, 0, ZoneOffset.ofTotalSeconds(getOffset())), Mapper.convert(LocalDateTime.of(2020, 1, 1, 2, 15), OffsetTime.class));
        assertEquals(OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(getOffset())), Mapper.convert(LocalDate.of(2020, 1, 1), OffsetDateTime.class));
        assertEquals(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/Sofia")), Mapper.convert(LocalDate.of(2020, 1, 1), ZonedDateTime.class));
        assertEquals(ZonedDateTime.of(2020, 1, 1, 2, 15, 0, 0, ZoneId.of("Europe/Sofia")), Mapper.convert(OffsetDateTime.of(2020, 1, 1, 2, 15, 0, 0, ZoneOffset.UTC), ZonedDateTime.class));
    }

    @Test
    void testToMap() {
        var test = new TestMap();
        test.setBaseString("base");
        test.setString1("test");
        test.setLong1(1L);
        test.setInt1(2);
        test.setConvert1(3);
        test.setConvert2(true);
        test.setMap(TestMap2.builder().string1("sub").long1(1L).int1(2).build());
        var map = Mapper.map(test, Map.class);
        assertEquals(8, map.size());
        assertEquals("base", map.get("baseString"));
        assertEquals("test", map.get("string1"));
        assertEquals(1L, map.get("long1"));
        assertEquals(2, map.get("int1"));
        assertEquals(3, map.get("convert1"));
        assertEquals(true, map.get("convert2"));
        assertNull(map.get("int2"));
        assertNotNull(map.get("map"));
        assertTrue(Map.class.isAssignableFrom(map.get("map").getClass()));
        var sub = (Map) map.get("map");
        assertEquals("sub", sub.get("string1"));
        assertEquals(1L, sub.get("long1"));
        assertEquals(2, sub.get("int1"));
    }

    @Test
    void testToMapSubNull() {
        var test = new TestMap();
        var map = Mapper.map(test, Map.class);
        assertEquals(8, map.size());
        assertNull(map.get("map"));
    }

    @Test
    void testNull() {
        assertNull(Mapper.map(null, TestMap.class));
        assertEquals(0, Mapper.map(null, int.class));
        assertNull(Mapper.convert(null, TestMap.class));
        assertEquals(0, Mapper.convert(null, int.class));

        assertThrows(NullPointerException.class, () -> Mapper.map(1, null));
        assertThrows(NullPointerException.class, () -> Mapper.convert(1, null));
    }

    @Test
    void testCollectionToList() {
        var result = Mapper.convert(List.of(1,2,3), List.class);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testArrayToList() {
        var result = Mapper.convert(new int[] {1,2,3}, List.class);

        assertNotNull(result);
        assertEquals(3, result.size());

        var result2 = Mapper.convert(new short[] {1,2,3}, List.class);

        assertNotNull(result2);
        assertEquals(3, result2.size());
    }

    @Test
    void testObjectToList() {
        var result = Mapper.convert(new MapperTest(), List.class);

        assertNotNull(result);
        assertEquals(1, result.size());

    }

    @Test
    void testListToArray() {
        var result = Mapper.convert(List.of(1,2,3), int[].class);

        assertNotNull(result);
        assertEquals(3, result.length);
    }

    @Test
    void testBooleanListToArray() {
        var result = Mapper.convert(List.of(true,false,true), boolean[].class);

        assertNotNull(result);
        assertEquals(3, result.length);
        assertTrue(result[0]);
        assertFalse(result[1]);
        assertTrue(result[2]);
    }

    @Test
    void testBooleanToInt() {
        var result = Mapper.convert(true, int.class);

        assertEquals(1, result);

        var f = Mapper.convert(true, float.class);

        assertEquals(1.0f, f);

    }

    private int getOffset() {
        return ZoneId.of("Europe/Sofia").getRules().getOffset(LocalDateTime.now()).getTotalSeconds();
    }

    private Temporal temporalConvert(Temporal source, Temporal destination) {
        destination = temporalConvertField(EPOCH_DAY, source, destination);
        destination = temporalConvertField(NANO_OF_DAY, source, destination);

        if (destination.isSupported(OFFSET_SECONDS)) {
            if (source.isSupported(OFFSET_SECONDS)) {
                destination = destination.with(OFFSET_SECONDS, source.getLong(OFFSET_SECONDS));
            } else {
                if (destination.isSupported(NANO_OF_DAY)) {
                    destination = destination.with(OFFSET_SECONDS, ZonedDateTime.now().getOffset().get(OFFSET_SECONDS));
                }
            }
        } else {
            if (source.isSupported(OFFSET_SECONDS) && destination.isSupported(NANO_OF_DAY)) {
                destination = destination.minus(ZonedDateTime.now().getOffset().get(OFFSET_SECONDS) + source.getLong(OFFSET_SECONDS), SECONDS);
            }
        }

        return destination;
    }

    private Temporal temporalConvertField(TemporalField field, Temporal source, Temporal destination) {
        if (destination.isSupported(field)) {
            if (source.isSupported(field)) {
                destination = destination.with(field, source.getLong(field));
            } else {
                destination = destination.with(field, 0);
            }
        }
        return destination;
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
