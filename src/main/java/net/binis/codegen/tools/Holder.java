package net.binis.codegen.tools;

/*-
 * #%L
 * code-generator
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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Holder<T> {

    private T object;
    private Supplier<T> supplier;

    public <R> Holder() {
        new Holder<R>(null);
    }

    public Holder(T object) {
        super();
        this.object = object;
    }

    public T get() {
        if (nonNull(supplier)) {
            object = supplier.get();
            supplier = null;
        }

        return object;
    }

    public void set(T object) {
        this.object = object;
    }

    public T update(T object) {
        this.object = object;
        return object;
    }

    public boolean isEmpty() {
        return isNull(object);
    }

    public boolean isPresent() {
        return nonNull(object);
    }

    public void ifPresent(Consumer<? super T> action) {
        if (object != null) {
            action.accept(object);
        }
    }

    public static <T> Holder<T> of(T object) {
        return new Holder<>(object);
    }

    public static <T> Holder<T> blank() {
        return new Holder<>((T) null);
    }

    public static <T> Holder<T> lazy(Supplier<T> supplier) {
        var result = new Holder<>((T) null);
        result.supplier = supplier;
        return result;
    }

    public String toString() {
        return (isNull(object)) ? "null" : object.toString();
    }

    public Holder<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (isEmpty()) {
            return this;
        } else {
            return predicate.test(object) ? this : blank();
        }
    }

    public <U> Holder<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return blank();
        } else {
            return Holder.of(mapper.apply(object));
        }
    }

    @SuppressWarnings("unchecked")
    public <U> Holder<U> flatMap(Function<? super T, ? extends Holder<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return blank();
        } else {
            return Objects.requireNonNull((Holder<U>) mapper.apply(object));
        }
    }

    public Stream<T> stream() {
        if (isEmpty()) {
            return Stream.empty();
        } else {
            return Stream.of(object);
        }
    }

}
