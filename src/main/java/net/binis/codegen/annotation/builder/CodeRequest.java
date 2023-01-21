package net.binis.codegen.annotation.builder;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 Binis Belev
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

import net.binis.codegen.annotation.CodeAnnotation;
import net.binis.codegen.enrich.*;
import net.binis.codegen.modifier.BaseModifier;
import net.binis.codegen.options.CodeOption;
import net.binis.codegen.options.GenerateOpenApiIfAvailableOption;
import net.binis.codegen.options.HiddenCreateMethodOption;
import net.binis.codegen.options.ValidationFormOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CodeAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface CodeRequest {

    String name() default "";

    String interfaceName() default "";

    boolean generateConstructor() default true;

    boolean generateImplementation() default true;

    boolean generateInterface() default true;

    boolean base() default false;

    boolean interfaceSetters() default false;

    boolean classGetters() default true;

    boolean classSetters() default false;

    Class<? extends BaseModifier> baseModifierClass() default BaseModifier.class;

    Class<?> mixInClass() default void.class;

    String implementationPackage() default "";

    String basePath() default "";

    String interfacePath() default "";

    String implementationPath() default "";

    Class<? extends Enricher>[] enrichers() default {ValidationEnricher.class, CreatorEnricher.class, OpenApiEnricher.class, JacksonEnricher.class, RegionEnricher.class};

    Class<? extends Enricher>[] inheritedEnrichers() default {};

    Class<? extends CodeOption>[] options() default {ValidationFormOption.class, HiddenCreateMethodOption.class, GenerateOpenApiIfAvailableOption.class};

}
