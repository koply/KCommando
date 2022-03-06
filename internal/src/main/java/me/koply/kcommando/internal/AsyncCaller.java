package me.koply.kcommando.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface AsyncCaller {
    ExecutorService executorService = Executors.newCachedThreadPool();
    ConcurrentMap<Long, Long> cooldownList = new ConcurrentHashMap<>();

    default void cooldownCleaner(long cooldown) {
        long current = System.currentTimeMillis();
        for (Map.Entry<Long, Long> entry : cooldownList.entrySet()) {
            if (current - entry.getValue() >= cooldown) {
                cooldownList.remove(entry.getKey());
            }
        }
    }
}