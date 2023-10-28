package net.binis.codegen.enrich;

/*-
 * #%L
 * code-generator
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

import net.binis.codegen.annotation.augment.AugmentTargetType;
import net.binis.codegen.annotation.augment.AugmentTargetTypeSeverity;
import net.binis.codegen.annotation.augment.AugmentType;
import net.binis.codegen.annotation.augment.CodeAugment;

import static java.lang.reflect.Modifier.*;

@CodeAugment(
        adds = AugmentType.FIELD,
        name = "log",
        type = "org.slf4j.Logger",
        modifier = PRIVATE | STATIC | FINAL,
        targets = AugmentTargetType.CLASS,
        severity = AugmentTargetTypeSeverity.NOTE,
        description = "Adds field: Logger log")
public interface LogEnricher extends Enricher {
}
