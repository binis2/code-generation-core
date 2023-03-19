package net.binis.codegen.tools;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2022 Binis Belev
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
import net.binis.codegen.factory.CodeFactory;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.nonNull;

@SuppressWarnings("unchecked")
public class Functional {

    public static <R> FunctionalDoWhile<R> _do() {
        return CodeFactory.create(FunctionalDoWhile.class);
    }

    public static <R> FunctionalDoWhile<R> _do(Supplier<R> supplier) {
        return _do()._with((Supplier) supplier);
    }

    public static <T, R> FunctionalRecursive<T, R> _recursive(T start) {
        return CodeFactory.create(FunctionalRecursive.class, start);
    }

    public static <I, R> FunctionalFor<I, R> _for(Iterable<I> iterable) {
        return CodeFactory.create(FunctionalFor.class, iterable);
    }

    public static <I, R> FunctionalFor<I, R> _for(Iterable<I> iterable, R initial) {
        return CodeFactory.create(FunctionalFor.class, iterable, initial);
    }


    protected Functional() {
        //Do nothing
    }

    public static class Initializer {

        public static <T> Supplier<List<T>> listOf(Class<T> cls) {
            return ArrayList::new;
        }

        public static <K, V> Supplier<Map<K, V>> mapOf(Class<K> keyClass, Class<V> valueClass) {
            return HashMap::new;
        }

        protected Initializer() {
            //Do nothing
        }

    }

    @Default("net.binis.codegen.tools.Functional$FunctionalDoWhileImpl")
    public interface FunctionalDoWhile<R> {
        FunctionalDoWhile<R> _run(Runnable runnable);
        FunctionalDoWhile<R> _with(Supplier<R> supplier);
        FunctionalEnd<R> _while(Predicate<R> predicate);
        FunctionalEnd<R> _while(BooleanSupplier supplier);
    }

    @Default("net.binis.codegen.tools.Functional$FunctionalRecursiveImpl")
    public interface FunctionalRecursive<T, R> {
        FunctionalRecursive<T, R> _on(UnaryOperator<T> on);
        <Q> FunctionalRecursive<T, Q> _init(Supplier<Q> init);
        <Q>FunctionalRecursive<T, Q> _init(Q init);
        void _perform(Consumer<T> doConsumer);
        FunctionalEnd<T> _do(UnaryOperator<T> doOperator);
        FunctionalEnd<R> _do(BiFunction<T, R, R> doFunction);
        FunctionalEnd<R> _perform(BiConsumer<T, R> perform);
    }

    @Default("net.binis.codegen.tools.Functional$FunctionalForImpl")
    public interface FunctionalFor<I, R> {
        R _do(BiFunction<I, R, R> function);
        R _do(Consumer<I> consumer);
    }

    public interface FunctionalEnd<R> {
        void _done();
        Optional<R> _get();
        <T> FunctionalEnd<T> _map(Function<R, T> mapper);
        FunctionalEnd<R> _then(Consumer<R> consumer);
    }

    protected static class FunctionalEndImpl<R> implements FunctionalEnd<R> {

        protected Object result = null;

        @Override
        public void _done() {
            //Do nothing
        }

        @Override
        public Optional _get() {
            return Optional.ofNullable(result);
        }

        @Override
        public FunctionalEnd _map(Function mapper) {
            result = mapper.apply(result);
            return this;
        }

        @Override
        public FunctionalEnd _then(Consumer consumer) {
            if (nonNull(result)) {
                consumer.accept(result);
            }
            return this;
        }
    }

    protected static class FunctionalDoWhileImpl<R> extends FunctionalEndImpl<R> implements FunctionalDoWhile<R> {

        protected Supplier<R> supplier = () -> null;

        {
            CodeFactory.registerType(FunctionalDoWhile.class, FunctionalDoWhileImpl::new, null);
        }

        @Override
        public FunctionalDoWhile<R> _run(Runnable runnable) {
            supplier = () -> {
                runnable.run();
                return null;
            };
            return this;
        }

        @Override
        public FunctionalDoWhile<R> _with(Supplier<R> supplier) {
            this.supplier = supplier;
            return this;
        }

        @Override
        public FunctionalEnd _while(Predicate predicate) {
            do {
                result = supplier.get();
            } while (predicate.test(result));

            return this;
        }

        @Override
        public FunctionalEnd<R> _while(BooleanSupplier supplier) {
            return _while(r -> supplier.getAsBoolean());
        }
    }

    protected static class FunctionalRecursiveImpl<T, R> extends FunctionalEndImpl<R> implements FunctionalRecursive<T, R> {

        protected UnaryOperator<T> on = null;
        protected T object;
        protected Supplier<R> init;

        {
            CodeFactory.registerType(FunctionalRecursive.class, p -> new FunctionalRecursiveImpl<>(p[0]), null);
        }

        protected FunctionalRecursiveImpl(T start) {
            this.object = start;
        }

        @Override
        public FunctionalRecursive<T, R> _on(UnaryOperator<T> on) {
            this.on = on;
            return this;
        }

        @Override
        public FunctionalRecursive _init(Supplier init) {
            this.init = init;
            return this;
        }

        @Override
        public FunctionalRecursive _init(Object init) {
            this.init = () -> (R) init;
            return this;
        }

        @Override
        public void _perform(Consumer<T> doConsumer) {
            assert nonNull(on) : "_on() operator is not specified!";
            var obj = object;
            while (nonNull(obj)) {
                obj = on.apply(obj);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public FunctionalEnd<T> _do(UnaryOperator<T> doOperator) {
            var obj = object;
            while (nonNull(obj)) {
                obj = doOperator.apply(obj);
            }

            result = (R) obj;

            return (FunctionalEnd) this;
        }

        @Override
        public FunctionalEnd<R> _do(BiFunction<T, R, R> doFunction) {
            assert nonNull(on) : "_on() operator is not specified!";
            var obj = object;
            R res = nonNull(init)? init.get() : null;
            while (nonNull(obj)) {
                res = doFunction.apply(obj, res);
                obj = on.apply(obj);
            }
            result = res;
            return this;
        }

        @Override
        public FunctionalEnd<R> _perform(BiConsumer<T, R> perform) {
            return _do((t, r) -> {
                perform.accept(t, r);
                return r;
            });
        }
    }

    protected static class FunctionalForImpl<I, R> implements FunctionalFor<I, R> {

        protected final Iterable<I> iterable;
        protected R result;

        {
            CodeFactory.registerType(FunctionalFor.class, p -> new FunctionalForImpl<>((Iterable) p[0], p.length == 2 ? (R) p[1] : null), null);
        }

        protected FunctionalForImpl(Iterable<I> iterable, R start) {
            this.iterable = iterable;
            this.result = start;
        }

        @Override
        public R _do(BiFunction<I, R, R> function) {
            for (var i : iterable) {
                result = function.apply(i, result);
            }

            return result;
        }

        @Override
        public R _do(Consumer<I> consumer) {
            for (var i : iterable) {
                consumer.accept(i);
            }
            return result;
        }

    }

}
