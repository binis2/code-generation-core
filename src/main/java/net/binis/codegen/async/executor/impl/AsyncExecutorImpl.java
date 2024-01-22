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
import net.binis.codegen.async.AsyncExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
public class AsyncExecutorImpl<T> extends BaseAsyncExecutorImpl<AsyncExecutor<T>, T> implements AsyncExecutor<T> {

    @Override
    public CompletableFuture<T> execute(Runnable task) {
        return internalExecute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Flow '{}' failed!", flow, e);
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<T> collect(Supplier<T> supplier) {
        return internalExecute(supplier);
    }


}
