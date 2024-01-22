package net.binis.codegen.async.executor.impl;

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

import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.async.AsyncDispatcher;
import net.binis.codegen.async.executor.CodeExecutor;
import net.binis.codegen.async.executor.CodeGenCompletableFuture;
import net.binis.codegen.factory.CodeFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

@SuppressWarnings("unchecked")
@Slf4j
public class BaseAsyncExecutorImpl<A, R> {

    protected String flow = CodeExecutor.DEFAULT;
    protected long delay;
    protected TimeUnit unit;

    public A flow(String flow) {
        this.flow = flow;
        return (A) this;
    }

    public A delay(long delay, TimeUnit unit) {
        this.delay = delay;
        this.unit = unit;
        return (A) this;
    }

    public A delay(Duration duration) {
        this.delay = duration.toMillis();
        this.unit = TimeUnit.MILLISECONDS;
        return (A) this;
    }

    protected CompletableFuture<R> internalExecute(Supplier<R> supplier) {
        var executor = CodeFactory.create(AsyncDispatcher.class).flow(flow);

        if (delay > 0 && nonNull(unit)) {
            executor = CompletableFuture.delayedExecutor(delay, unit, executor);
        }

        return CodeGenCompletableFuture.newSupplyAsync(executor, supplier);
    }
}
