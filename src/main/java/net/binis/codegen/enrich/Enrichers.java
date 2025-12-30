package net.binis.codegen.enrich;

/*-
 * #%L
 * code-generator-core
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

public final class Enrichers {

    private Enrichers() {
        //Do nothing
    }

    public static final Class<? extends Enricher> AS = AsEnricher.class;
    public static final Class<? extends Enricher> LOG = LogEnricher.class;
    public static final Class<? extends Enricher> CLONE = CloneEnricher.class;
    public static final Class<? extends Enricher> CREATOR = CreatorEnricher.class;
    public static final Class<? extends Enricher> CREATOR_MODIFIER = CreatorModifierEnricher.class;
    public static final Class<? extends Enricher> FLUENT = FluentEnricher.class;
    public static final Class<? extends Enricher> MODIFIER = ModifierEnricher.class;
    public static final Class<? extends Enricher> QUERY = QueryEnricher.class;
    public static final Class<? extends Enricher> REGION = RegionEnricher.class;
    public static final Class<? extends Enricher> VALIDATION = ValidationEnricher.class;
    public static final Class<? extends Enricher> OPENAPI = OpenApiEnricher.class;
    public static final Class<? extends Enricher> JACKSON = JacksonEnricher.class;
    public static final Class<? extends Enricher> HIBERNATE = HibernateEnricher.class;

}
