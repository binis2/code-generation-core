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
import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.annotation.type.GenerationStrategy;
import net.binis.codegen.config.DefaultMappings;
import net.binis.codegen.map.Mapper;
import net.binis.codegen.map.MapperFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.*;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MapperTest {

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
        Mapper.map().source(TestMap.class).destination(TestMap2.class);
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
    void testEnum() {
        assertEquals(GenerationStrategy.CLASSIC, Mapper.convert("CLASSIC", GenerationStrategy.class));
        assertEquals(GenerationStrategy.IMPLEMENTATION, Mapper.convert("IMPLEMENTATION", GenerationStrategy.class));
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
        assertEquals(OffsetTime.of(2, 15, 0, 0, ZoneOffset.ofTotalSeconds(7200)), Mapper.convert(LocalDateTime.of(2020, 1, 1, 2, 15), OffsetTime.class));
        assertEquals(OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(7200)), Mapper.convert(LocalDate.of(2020, 1, 1), OffsetDateTime.class));
        assertEquals(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/Sofia")), Mapper.convert(LocalDate.of(2020, 1, 1), ZonedDateTime.class));
        assertEquals(ZonedDateTime.of(2020, 1, 1, 2, 15, 0, 0, ZoneId.of("Europe/Sofia")), Mapper.convert(OffsetDateTime.of(2020, 1, 1, 2, 15, 0, 0, ZoneOffset.UTC), ZonedDateTime.class));
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
        private String string1;
        private Long long1;
        private int int1;
        private Integer int2;

        private int convert1;
        private boolean convert2;
    }

    @Data
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
