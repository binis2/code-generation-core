package net.binis.codegen.enrich.field;

/*-
 * #%L
 * code-generator
 * %%
 * Copyright (C) 2021 - 2026 Binis Belev
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

import net.binis.codegen.annotation.Default;
import net.binis.codegen.annotation.augment.*;
import net.binis.codegen.enrich.Enricher;

@CodeAugment(
        adds = AugmentType.GETTER,
        targets = {AugmentTargetType.CLASS, AugmentTargetType.FIELD},
        severity = AugmentTargetTypeSeverity.WARNING,
        parameters = @CodeAugmentParameters(
                filter = "FIELDS|!STATIC"
        ),
        description = "Adds <b>getters</b> for all <u>non static</u> fields|Adds <b>getter</b>")
@Default("net.binis.codegen.enrich.handler.field.GetterEnricherHandler")
public interface GetterEnricher extends Enricher {
}
