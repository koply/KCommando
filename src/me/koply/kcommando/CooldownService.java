package me.koply.kcommando;

import java.util.Map;
import java.util.concurrent.*;

class CooldownService {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final ConcurrentMap<String, Long> cooldownMap;
    private final long cooldown;

    public CooldownService(ConcurrentMap<String, Long> map, long cooldown) {
        cooldownMap = map;
        this.cooldown = cooldown;
    }

    public final void asyncCleaner() {
        final Runnable task = this::cleaner;
        scheduledExecutorService.scheduleAtFixedRate(task, 1L, 1L, TimeUnit.MINUTES);
    }

    private void cleaner() {
        long currentMillis = System.currentTimeMillis();
        int i = 0;
        for (Map.Entry<String, Long> entry : cooldownMap.entrySet()) {
            if (currentMillis - entry.getValue() >= cooldown) {
                cooldownMap.remove(entry.getKey());
                i++;
            }
        }
        if (i==0) return;
        KCommando.logger.info("#CooldownService: Removed " + i + " entries from cooldown list.");
    }
}