package net.binis.codegen.discovery;

/*-
 * #%L
 * code-generator
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

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.annotation.CodeConfiguration;

import java.io.*;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.binis.codegen.tools.Reflection.loadClass;

@Slf4j
public abstract class Discoverer {

    public static final String TEMPLATE = "template";
    public static final String CONFIG = "config";
    protected static final String RESOURCE_PATH = "binis/annotations";

    protected static List<InputStream> loadResources(
            final String name, final ClassLoader classLoader) throws IOException {
        var list = new ArrayList<InputStream>();
        var systemResources =
                (classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader)
                        .getResources(name);
        while (systemResources.hasMoreElements()) {
            list.add(systemResources.nextElement().openStream());
        }
        return list;
    }

    public static List<DiscoveredService> findAnnotations(String text) {
        var result = new ArrayList<DiscoveredService>();
        Discoverer.processResource(text, result);
        return result;
    }

    public static List<DiscoveredService> findAnnotations() {
        var result = new ArrayList<DiscoveredService>();
        try {
            loadResources(RESOURCE_PATH, Discoverer.class.getClassLoader()).forEach(s -> Discoverer.processResource(s, result));
        } catch (Exception e) {
            log.error("Unable to discover services!");
        }
        return result;
    }

    protected static void processResource(String text, List<DiscoveredService> services) {
        processResource(new BufferedReader(new StringReader(text)), services, false, false);
    }

    protected static void processResource(InputStream stream, List<DiscoveredService> services) {
        processResource(new BufferedReader(new InputStreamReader(stream)), services, true, !(stream instanceof BufferedInputStream));
    }

    @SuppressWarnings("unchecked")
    protected static void processResource(BufferedReader reader, List<DiscoveredService> services, boolean tryLoad, boolean showWarning) {
        try {
            while (reader.ready()) {
                var line = reader.readLine();
                if (isNull(line)) {
                    break;
                }
                var parts = line.split(":");
                if (parts.length == 2) {
                    if (TEMPLATE.equals(parts[0])) {
                        if (tryLoad) {
                            var cls = loadClass(parts[1]);
                            if (nonNull(cls)) {
                                if (Annotation.class.isAssignableFrom(cls)) {
                                    services.add(DiscoveredService.builder().type(parts[0]).name(parts[1]).cls(cls).build());
                                }
                            } else {
                                if (showWarning) {
                                    log.warn("Can't load class: {}!", parts[1]);
                                }
                            }
                        } else {
                            services.add(DiscoveredService.builder().type(parts[0]).name(parts[1]).cls(null).build());
                        }
                    } else if (CONFIG.equals(parts[0])) {
                        if (tryLoad) {
                            var cls = loadClass(parts[1]);
                            if (nonNull(cls)) {
                                if (cls.isAnnotationPresent(CodeConfiguration.class)) {
                                    services.add(DiscoveredService.builder().type(parts[0]).name(parts[1]).cls(cls).build());
                                }
                            } else {
                                log.warn("Can't load class: {}!", parts[1]);
                            }
                        } else {
                            services.add(DiscoveredService.builder().type(parts[0]).name(parts[1]).cls(null).build());
                        }
                    } else {
                        log.warn("Invalid descriptor type: {}!", parts[0]);
                    }
                } else {
                    log.warn("Invalid descriptor line: {}!", line);
                }
            }
        } catch (IOException e) {
            log.warn("Failed to process stream!", e);
        }
    }

    protected static Set<String> readServiceFile(InputStream input) throws IOException {
        var serviceClasses = new HashSet<String>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                int commentStart = line.indexOf('#');
                if (commentStart >= 0) {
                    line = line.substring(0, commentStart);
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    serviceClasses.add(line);
                }
            }
            return serviceClasses;
        }
    }

    @Builder
    @Data
    public static class DiscoveredService {
        protected String type;
        protected String name;
        protected Class<?> cls;

        public boolean isConfig() {
            return CONFIG.equals(type);
        }

        public boolean isTemplate() {
            return TEMPLATE.equals(type);
        }

    }
}
