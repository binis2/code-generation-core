package net.binis.codegen.config;

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

import net.binis.codegen.annotation.CodeConfiguration;
import net.binis.codegen.exception.MapperException;
import net.binis.codegen.factory.CodeFactory;
import net.binis.codegen.map.Mapper;
import net.binis.codegen.objects.base.enumeration.CodeEnum;

import java.io.*;

@SuppressWarnings("unchecked")
@CodeConfiguration
public abstract class DefaultMappings {

    public static void initialize() {
        //Creation of primitive types and wrappers
        CodeFactory.registerType(int.class, () -> 0);
        CodeFactory.registerType(long.class, () -> 0L);
        CodeFactory.registerType(byte.class, () -> (byte) 0);
        CodeFactory.registerType(short.class, () -> (short) 0);
        CodeFactory.registerType(boolean.class, () -> false);
        CodeFactory.registerType(char.class, () -> (char) 0);
        CodeFactory.registerType(float.class, () -> (float) 0.0);
        CodeFactory.registerType(double.class, () -> 0.0);
        CodeFactory.registerType(Integer.class, () -> 0);
        CodeFactory.registerType(Long.class, () -> 0L);
        CodeFactory.registerType(Byte.class, () -> (byte) 0);
        CodeFactory.registerType(Short.class, () -> (short) 0);
        CodeFactory.registerType(Boolean.class, () -> false);
        CodeFactory.registerType(Character.class, () -> (char) 0);
        CodeFactory.registerType(Float.class, () -> (float) 0.0);
        CodeFactory.registerType(Double.class, () -> 0.0);
        CodeFactory.registerType(String.class, () -> "");

        //Number conversions
        Mapper.registerMapper(Number.class, int.class, (s, d) -> s.intValue());
        Mapper.registerMapper(Number.class, Integer.class, (s, d) -> s.intValue());
        Mapper.registerMapper(Number.class, long.class, (s, d) -> s.longValue());
        Mapper.registerMapper(Number.class, Long.class, (s, d) -> s.longValue());
        Mapper.registerMapper(Number.class, byte.class, (s, d) -> s.byteValue());
        Mapper.registerMapper(Number.class, Byte.class, (s, d) -> s.byteValue());
        Mapper.registerMapper(Number.class, short.class, (s, d) -> s.shortValue());
        Mapper.registerMapper(Number.class, Short.class, (s, d) -> s.shortValue());
        Mapper.registerMapper(Number.class, boolean.class, (s, d) -> s.intValue() != 0);
        Mapper.registerMapper(Number.class, Boolean.class, (s, d) -> s.intValue() != 0);
        Mapper.registerMapper(Number.class, char.class, (s, d) -> (char) s.intValue());
        Mapper.registerMapper(Number.class, Character.class, (s, d) -> (char) s.intValue());
        Mapper.registerMapper(Number.class, float.class, (s, d) -> s.floatValue());
        Mapper.registerMapper(Number.class, Float.class, (s, d) -> s.floatValue());
        Mapper.registerMapper(Number.class, double.class, (s, d) -> s.doubleValue());
        Mapper.registerMapper(Number.class, Double.class, (s, d) -> s.doubleValue());
        //String conversion
        Mapper.registerMapper(Object.class, String.class, (s, d) -> s.toString());
        //Enum conversion
        Mapper.registerMapperClass(String.class, Enum.class, (s, d) -> Enum.valueOf(d, s));
        Mapper.registerMapperClass(String.class, CodeEnum.class, (s, d) -> CodeFactory.enumValueOf(d, s));
        Mapper.registerMapperClass(Number.class, CodeEnum.class, (s, d) -> CodeFactory.enumValueOf(d, s.intValue()));
        //Java Serialization
        Mapper.registerMapperClass(byte[].class, Serializable.class, (s, d) -> {
            try (ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(s))) {
                return (Serializable) is.readObject();
            } catch (Exception e) {
                throw new MapperException(e);
            }
        });
        Mapper.registerMapperClass(Serializable.class, byte[].class, (s, d) -> {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream os = new ObjectOutputStream(bos)) {
                os.writeObject(s);
                return bos.toByteArray();
            } catch (Exception e) {
                throw new MapperException(e);
            }
        });
    }

    private DefaultMappings() {
        //Do nothing
    }

}

