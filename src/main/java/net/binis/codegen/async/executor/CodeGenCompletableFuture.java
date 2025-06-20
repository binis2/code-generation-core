package net.binis.codegen.async.executor;

/*-
 * #%L
 * code-generator-spring
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class CodeGenCompletableFuture<T> extends CompletableFuture<T> {

    protected final Executor executor;

    public CodeGenCompletableFuture(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Executor defaultExecutor() {
        return executor;
    }

    @Override
    public <U> CompletableFuture<U> newIncompleteFuture() {
        return new CodeGenCompletableFuture<>(executor);
    }

    public static CompletableFuture<Void> runAsync(Executor executor, Runnable runnable) {
        Objects.requireNonNull(runnable);
        return newSupplyAsync(executor, () -> {
            runnable.run();
            return null;
        });
    }

    public static <U> CompletableFuture<U> newSupplyAsync(Executor executor, Supplier<U> supplier) {
        return new CodeGenCompletableFuture<U>(executor).completeAsync(supplier);
    }

}
