package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.model.Income;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class IncomeSchedulerService {

    @Autowired
    private TaskScheduler taskScheduler;

//    public void schedulerIncomeAddition(Income income, Runnable task) {
//        String interval = income.getInterval();
//        if (interval != null && !interval.isEmpty()) {
//            long intervalMillis = convertIntervalToMillis(interval);
//
//            taskScheduler.scheduleAtFixedRate(task, intervalMillis);
//            System.out.println("Scheduled recurring addition every " + intervalMillis + " ms for income: " + income.getTitle());
//        }else{
//            System.out.println("interval is null or empty");
//        }
//    }
//
//    public void cancelScheduledIncomeTask(UUID incomeId){
//        taskScheduler.
//    }

    private final Map<UUID, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * Schedule recurring income addition.
     */
    public void schedulerIncomeAddition(Income income, Runnable task) {
        String interval = income.getInterval();
        if (interval != null && !interval.isEmpty()) {
            long intervalMillis = convertIntervalToMillis(interval);

            // Schedule the task
            ScheduledFuture<?> scheduledTask = taskScheduler.scheduleAtFixedRate(task, intervalMillis);

            // Store the scheduled task for later cancellation
            scheduledTasks.put(income.getIncome_id(), scheduledTask);

            System.out.println("Scheduled recurring addition every " + intervalMillis + " ms for income: " + income.getTitle());
        } else {
            System.out.println("Interval is null or empty, no task scheduled.");
        }
    }

    /**
     * Cancel the scheduled task for a specific income.
     */
    public void cancelScheduledIncomeTask(UUID incomeId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(incomeId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false); // Cancel without interrupting if running
            scheduledTasks.remove(incomeId);
            System.out.println("Canceled scheduled task for income ID: " + incomeId);
        } else {
            System.out.println("No scheduled task found for income ID: " + incomeId);
        }
    }


    private long convertIntervalToMillis(String interval) {
        switch (interval.toLowerCase()) {
            case "5s":
                return 5000; // 5 seconds
            case "10s":
                return 10000; // 10 seconds
            case "day":
                return 86400000; // 1 day
            case "week":
                return 604800000; // 1 week
            case "month":
                return 2592000000L; // 30 days (approximation)
            default:
                throw new IllegalArgumentException("Invalid interval: " + interval);
        }
    }

//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
//
//    public void schedulerIncomeAddition(Income income,Runnable task){
//        long interval = parseInterval(income.getInterval());
//
//        if (interval > 0){
//            scheduler.scheduleAtFixedRate(task,0,interval, TimeUnit.SECONDS);
//        }
//    }
//
//    private long parseInterval(String interval) {
//        switch (interval.toLowerCase()){
//            case "5s": return 5;
//            case "10s": return 10;
//            case "day": return 24 * 60 * 60;
//            case "week": return 7 * 24 * 60 *60;
//            case "month": return 30 * 24 * 60 * 60;
//            default: return 0;
//        }
//    }
}
