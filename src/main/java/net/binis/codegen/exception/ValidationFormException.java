package net.binis.codegen.exception;

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

import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
public class ValidationFormException extends RuntimeException {

    private final Map<String, List<String>> errors;
    private final Class<?> cls;

    public ValidationFormException(Class<?> cls, Map<String, List<String>> errors) {
        super();
        this.errors = errors;
        this.cls = cls;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public Class<?> getFormClass() {
        return cls;
    }

}