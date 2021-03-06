/*
 * Copyright 2016 The Simple File Server Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sfs.util;

import io.vertx.core.Context;
import org.sfs.Server;
import org.sfs.SfsVertx;
import org.sfs.VertxContext;
import org.sfs.rx.ObservableFuture;
import org.sfs.rx.RxHelper;
import org.sfs.rx.SingleAsyncResultSubscriber;
import rx.Observable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.nio.file.Files.createFile;

public class FileSystemLock {

    private final Path name;
    private final long timeout;

    public FileSystemLock(Path lockFileName, long timeout, TimeUnit timeUnit) {
        this.name = lockFileName;
        this.timeout = timeUnit.toMillis(timeout);
    }

    public Observable<Boolean> tryLock(VertxContext<Server> vertxContext) {
        SfsVertx sfsVertx = vertxContext.vertx();
        Context context = sfsVertx.getOrCreateContext();
        return RxHelper.executeBlocking(context, sfsVertx.getBackgroundPool(), () -> {
            try {
                createFile(name);
                name.toFile().deleteOnExit();
                return true;
            } catch (IOException e) {
                return false;
            }
        });
    }

    public Observable<Void> lock(VertxContext<Server> vertxContext) {
        ObservableFuture<Void> handler = RxHelper.observableFuture();
        lock(vertxContext, currentTimeMillis(), handler);
        return handler
                .map(aVoid -> {
                    name.toFile().deleteOnExit();
                    return null;
                });
    }

    private void lock(VertxContext<Server> vertxContext, long startTime, ObservableFuture<Void> handler) {
        SfsVertx sfsVertx = vertxContext.vertx();
        Context context = sfsVertx.getOrCreateContext();
        RxHelper.executeBlocking(context, sfsVertx.getBackgroundPool(),
                () -> {
                    try {
                        createFile(name);
                    } catch (IOException e) {
                        if (currentTimeMillis() - startTime >= timeout) {
                            throw new LockWaitTimeout(format("Failed to obtain lock on %s after %dms", name, timeout));
                        } else {
                            vertxContext.vertx().setTimer(100, event -> lock(vertxContext, startTime, handler));
                        }
                    }
                    return (Void) null;
                })
                .subscribe(new SingleAsyncResultSubscriber<>(handler));
    }


    public static class LockWaitTimeout extends RuntimeException {
        public LockWaitTimeout(String message) {
            super(message);
        }
    }
}
