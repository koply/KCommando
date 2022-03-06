package me.koply.kcommando.internal;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CronService {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final ArrayList<CronJob> jobs = new ArrayList<>();

    private static CronService instance;
    public static CronService getInstance() {
        if (instance == null) instance = new CronService();
        return instance;
    }

    private CronService() {
        asyncInitializer();
    }

    protected void asyncInitializer() {
        final Runnable task = this::task;
        scheduledExecutorService.scheduleAtFixedRate(task, 1L, 1L, TimeUnit.MINUTES);
    }

    protected void task() {
        for (CronJob job : jobs) {
            Runnable r = job.getToRun();
            if (r != null) r.run();
        }
    }

    public void addRunnable(Runnable r, int timeAsMinutes) {
        jobs.add(new CronJob(r, timeAsMinutes));
    }

    private static class CronJob {
        private final Runnable runnable;
        private final int timeAsMinutes;
        private int minutesLeft;

        CronJob(Runnable runnable, int timeAsMinutes) {
            this.runnable = runnable;
            this.timeAsMinutes = timeAsMinutes;
            minutesLeft = timeAsMinutes;
        }

        Runnable getToRun() {
            minutesLeft -= 1;
            if (minutesLeft == 0) {
                minutesLeft = timeAsMinutes;
                return runnable;
            } else {
                return null;
            }
        }
    }
}