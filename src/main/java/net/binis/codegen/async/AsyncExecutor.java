package net.binis.codegen.async;

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

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface AsyncExecutor<R> {

    AsyncExecutor<R> flow(String flow);
    AsyncExecutor<R> delay(long delay, TimeUnit unit);
    AsyncExecutor<R> delay(Duration duration);
    AsyncExecutor<R> lock(Object lock);
    CompletableFuture<R> execute(Runnable task);
    CompletableFuture<R> collect(Supplier<R> supplier);

}
