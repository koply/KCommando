package me.koply.kcommando;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class CronService {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final ArrayList<Runnable> runnables = new ArrayList<>();

    private static CronService instance;
    public static CronService getInstance() {
        if (instance == null) instance = new CronService();
        return instance;
    }

    private CronService() {
        asyncInitializer();
    }

    protected final void asyncInitializer() {
        final Runnable task = this::task;
        scheduledExecutorService.scheduleAtFixedRate(task, 1L, 1L, TimeUnit.MINUTES);
    }

    protected final void task() {
        for (Runnable r : runnables) {
            r.run();
        }
    }

    public final void addRunnable(Runnable...r) {
        runnables.addAll(Arrays.asList(r));
    }
}