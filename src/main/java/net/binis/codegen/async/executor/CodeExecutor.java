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

import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.async.AsyncDispatcher;
import net.binis.codegen.async.monitoring.DispatcherMonitor;
import net.binis.codegen.factory.CodeFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Slf4j
public class CodeExecutor {

    public static final String DEFAULT = "default";

    private static final Dispatcher dispatcher = new Dispatcher();

    static {
        CodeFactory.registerType(AsyncDispatcher.class, CodeFactory.singleton(CodeExecutor.defaultDispatcher()), null);
        registerDefaultExecutor(Executors.defaultExecutor(DEFAULT));
    }

    public CodeExecutor() {
        //Do nothing.
    }

    public static void registerDefaultExecutor(Executor executor) {
        registerExecutor(DEFAULT, executor);
    }

    public static void registerExecutor(String flow, Executor executor) {
        dispatcher.register(flow, executor);
    }

    public static AsyncDispatcher defaultDispatcher() {
        return dispatcher;
    }

    private static final class Dispatcher implements AsyncDispatcher, DispatcherMonitor {
        private final Map<String, Executor> flows = new ConcurrentHashMap<>();

        @Override
        public Executor flow(String flow) {
            return flows.computeIfAbsent(flow, Executors::defaultExecutor);
        }

        @Override
        public Executor _default() {
            return flow(DEFAULT);
        }

        private void register(String flow, Executor executor) {
            flows.put(flow, executor);
        }

        @Override
        public Executor getExecutor(String flow) {
            return flows.get(flow);
        }

        @Override
        public Executor getDefaultExecutor() {
            return flows.get(DEFAULT);
        }

        @Override
        public Set<String> flows() {
            return flows.keySet();
        }
    }

}
