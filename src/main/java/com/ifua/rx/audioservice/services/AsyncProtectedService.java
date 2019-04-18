package com.ifua.rx.audioservice.services;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.Callable;

public interface AsyncProtectedService {
    default <T> Mono<T> async(Callable<T> callable) {
        return Mono.fromCallable(callable).publishOn(getScheduler());
    }

    Scheduler getScheduler();
}
