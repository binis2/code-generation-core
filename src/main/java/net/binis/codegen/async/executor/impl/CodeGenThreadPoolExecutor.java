package net.binis.codegen.async.executor.impl;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2026 Binis Belev
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

import net.binis.codegen.async.monitoring.ExecutorMonitor;

import java.util.concurrent.*;

import static java.util.Objects.nonNull;

public class CodeGenThreadPoolExecutor extends ThreadPoolExecutor implements Executor, ExecutorMonitor {

    private final Executor executor;

    public CodeGenThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, Executor executor) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.executor = executor;
    }

    public CodeGenThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, Executor executor) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.executor = executor;
    }

    public CodeGenThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler, Executor executor) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.executor = executor;
    }

    public CodeGenThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, Executor executor) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        super.execute(() -> executor.execute(command));
    }

    @Override
    public long getQueueSize() {
        if (nonNull(getQueue())) {
            return getQueue().size();
        }
        return 0;
    }
}
