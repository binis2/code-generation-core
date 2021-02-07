package net.binis.codegen.annotation;

import net.binis.codegen.enrich.Enricher;

@CodeAnnotation
public @interface CodeEnricher {
    Class<Enricher> enrichClass();
}
