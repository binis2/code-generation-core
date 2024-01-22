package net.binis.codegen.async.executor;

/*-
 * #%L
 * code-generator-core
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
import net.binis.codegen.async.executor.impl.CodeGenThreadPoolExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class Executors {

    private static final RejectedExecutionHandler defaultHandler =
            new ThreadPoolExecutor.AbortPolicy();

    public static Executor wrappedExecutor(String flow, Executor task) {
        BlockingQueue<Runnable> queue = new LinkedTransferQueue<>() {
            @Override
            public boolean offer(Runnable e) {
                return tryTransfer(e);
            }
        };

        var executor = new CodeGenThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(),
                60L, TimeUnit.SECONDS,
                queue,
                new DefaultThreadFactory(flow),
                defaultHandler,
                task);

        executor.setRejectedExecutionHandler((r, ex) -> {
            try {
                ex.getQueue().put(r);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        return executor;
    }

    public static Executor defaultExecutor(String flow) {
        return wrappedExecutor(flow, defaultTask(flow));
    }

    public static Executor fixedThreadPool(String flow, int nThreads) {
        return new CodeGenThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new DefaultThreadFactory(flow),
                defaultHandler,
                defaultTask(flow));
    }

    public static Executor fixedThreadPool(String flow, int nThreads, int queueSize) {
        return new CodeGenThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueSize),
                new DefaultThreadFactory(flow),
                defaultHandler,
                defaultTask(flow));
    }

    public static Executor singleThreadedExecutor(String flow) {
        return fixedThreadPool(flow, 1);
    }

    public static Executor singleThreadedExecutor(String flow, int queueSize) {
        return fixedThreadPool(flow, 1, queueSize);
    }

    public static Executor silentExecutor(String flow) {
        return wrappedExecutor(flow, task -> {
            try {
                task.run();
            } catch (Exception e) {
                //Do nothing
            }
        });
    }

    public static Executor syncExecutor() {
        return Runnable::run;
    }

    public static Executor syncSilentExecutor() {
        return task -> {
            try {
                task.run();
            } catch (Exception e) {
                log.warn("Failed to execute task!", e);
            }
        };
    }

    private static Executor defaultTask(String flow) {
        return task -> {
            try {
                task.run();
            } catch (Exception e) {
                log.warn("Failed to execute task for flow ({})!", flow, e);
            }
        };
    }

    protected static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String flow) {
            var s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = flow + "-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
