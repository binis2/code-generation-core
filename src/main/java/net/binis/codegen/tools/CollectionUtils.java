package net.binis.codegen.tools;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;

public class CollectionUtils {

    private CollectionUtils() {
        //Do nothing
    }

    public static <T> List<T> copyList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    public static boolean isEmpty(Collection collection) {
        return isNull(collection) || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return nonNull(collection) && !collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return nonNull(map) && !map.isEmpty();
    }

    public static String printInfo(Object object, boolean full) {
        if (isNull(object)) {
            return "null";
        } else {
            if (object instanceof Collection<?> collection) {
                if (full) {
                    return collection.getClass().getSimpleName() + "[" + collection.stream().map(o -> isNull(o) ? "null" : o.toString()).collect(joining(", ")) + "]";
                }
                return collection.getClass().getSimpleName() + "[" + collection.size() + "]";
            } else if (object instanceof Map<?, ?> map) {
                if (full) {
                    return map.getClass().getSimpleName() + "[" + map.entrySet().stream().map(e -> "{" + e.getKey().toString() + ": " + printInfo(e.getValue(), true) + "}").collect(joining(", ")) + "]";
                }
                return map.getClass().getSimpleName() + "[" + map.size() + "]";
            } else {
                return object.toString();
            }
        }
    }

}
