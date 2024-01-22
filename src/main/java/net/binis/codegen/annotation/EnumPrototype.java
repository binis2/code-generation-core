package net.binis.codegen.annotation;

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

import net.binis.codegen.enrich.Enricher;
import net.binis.codegen.enrich.constructor.RequiredArgsConstructorEnricher;
import net.binis.codegen.options.CodeOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CodeAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnumPrototype {

    String name() default "";
    Class<?> mixIn() default void.class;
    int ordinalOffset() default 0;

    Class<? extends Enricher>[] enrichers() default {RequiredArgsConstructorEnricher.class};

    Class<? extends CodeOption>[] options() default {};

}
