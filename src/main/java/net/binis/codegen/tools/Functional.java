package net.binis.codegen.tools;

import net.binis.codegen.annotation.Default;
import net.binis.codegen.factory.CodeFactory;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class Functional {

    public static <R> FunctionalDoWhile<R> _do() {
        return CodeFactory.create(FunctionalDoWhile.class);
    }

    public static <R> FunctionalDoWhile<R> _do(Supplier<R> supplier) {
        return _do()._with((Supplier) supplier);
    }

    protected Functional() {
        //Do nothing
    }

    @Default("net.binis.codegen.tools.Functional$FunctionalDoWhileImpl")
    public interface FunctionalDoWhile<R> {
        FunctionalDoWhile<R> _run(Runnable runnable);
        FunctionalDoWhile<R> _with(Supplier<R> supplier);
        FunctionalEnd<R> _while(Function<R, Boolean> func);
        FunctionalEnd<R> _while(Supplier<Boolean> supplier);
    }

    public interface FunctionalEnd<R> {
        void _done();
        R _get();
        void _then(Consumer<R> consumer);
    }

    protected static class FunctionalDoWhileImpl<R> implements FunctionalDoWhile<R>, FunctionalEnd<R> {

        protected Supplier<R> supplier = () -> null;
        protected R result = null;

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
        public FunctionalEnd<R> _while(Function<R, Boolean> func) {
            do {
                result = supplier.get();
            } while (func.apply(result));

            return this;
        }

        @Override
        public FunctionalEnd<R> _while(Supplier<Boolean> supplier) {
            return _while(r -> supplier.get());
        }

        @Override
        public void _done() {
            //Do nothing
        }

        @Override
        public R _get() {
            return result;
        }

        @Override
        public void _then(Consumer<R> consumer) {
            consumer.accept(result);
        }
    }


}
