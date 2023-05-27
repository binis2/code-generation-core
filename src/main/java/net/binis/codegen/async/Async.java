package net.binis.codegen.async;

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

import net.binis.codegen.async.executor.CodeExecutor;
import net.binis.codegen.async.executor.impl.AsyncExecutorImpl;
import net.binis.codegen.factory.CodeFactory;

import java.util.concurrent.Executor;
import java.util.function.Function;

public class Async {

    static {
        CodeFactory.registerType(AsyncDispatcher.class, CodeFactory.singleton(CodeExecutor.defaultDispatcher()));
        CodeFactory.registerType(AsyncExecutor.class, AsyncExecutorImpl::new);
    }

    @SuppressWarnings("unchecked")
    public static <T> AsyncExecutor<T> start() {
        return CodeFactory.create(AsyncExecutor.class);
    }

    public static void registerFlow(String name, Executor executor) {
        CodeExecutor.registerExecutor(name, executor);
    }

    public static void registerFlow(String name, Function<String, Executor> executor) {
        CodeExecutor.registerExecutor(name, executor.apply(name));
    }

    private Async() {
        //Do nothing
    }

}
