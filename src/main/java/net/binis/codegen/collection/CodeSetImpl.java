package net.binis.codegen.collection;

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

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class CodeSetImpl<T, R> implements CodeSet<T, R> {

    private final R parent;
    private final Set<T> set;
    private final Consumer<T> validator;

    public CodeSetImpl(R parent, Set<T> set) {
        this.parent = parent;
        this.set = set;
        this.validator = null;
    }

    public CodeSetImpl(R parent, Set<T> set, Consumer<T> validator) {
        this.parent = parent;
        this.set = set;
        this.validator = validator;
    }

    @Override
    public CodeSet<T, R> add(T value) {
        validate(value);
        set.add(value);
        return this;
    }

    @Override
    public R and() {
        return parent;
    }

    protected void validate(T value) {
        if (nonNull(validator)) {
            validator.accept(value);
        }
    }

}
