package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.model.Income;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.*;

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

//    public void cancelScheduledIncomeTask(UUID incomeId){
//        taskScheduler.
//    }

    private final Map<UUID, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * Schedule recurring income addition.
     */
    public void schedulerIncomeAddition(Income income,String interval, Runnable task) {
//        String interval = income.getInterval();
        System.out.println("interval inside scheduler: " + interval);
//        if (interval != null && !interval.isEmpty()) {
//            long intervalMillis = convertIntervalToMillis(interval);
//
//            // Schedule the task
//            ScheduledFuture<?> scheduledTask = taskScheduler.scheduleAtFixedRate(task, intervalMillis);
//
//            // Store the scheduled task for later cancellation
//            scheduledTasks.put(income.getIncome_id(), scheduledTask);
//
//            System.out.println("Scheduled recurring addition every " + intervalMillis + " ms for income: " + income.getTitle());
//        } else {
//            System.out.println("Interval is null or empty, no task scheduled.");
//        }
//
        long intervalMillis;
        try{
            intervalMillis = convertIntervalToMillis(interval);
            System.out.println("converted interval into millis" + intervalMillis);
        }catch (Exception e){
            System.out.println("error converting interval" + e.getMessage());
            return;
        }

        System.out.println("taskscheduler instance: " + taskScheduler);
        System.out.println("before schedule task: ");

        PeriodicTrigger trigger = new PeriodicTrigger(intervalMillis,TimeUnit.MILLISECONDS);
        trigger.setInitialDelay(intervalMillis);
//        ScheduledFuture<?> scheduledTask = taskScheduler.scheduleAtFixedRate(task,intervalMillis);
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, trigger);
//        ScheduledFuture<?> scheduledTask = taskScheduler.scheduleWithFixedDelay(task, intervalMillis);

        System.out.println("after schedule task: " + scheduledTask);

        if(scheduledTask == null){
            System.out.println("error: task scheduler return null");
            return;
        }
        System.out.println("id before mapping: " + income.getIncome_id());
        scheduledTasks.put(income.getIncome_id(),scheduledTask);
        System.out.println("schedule tasks mapping: " + scheduledTasks);
        System.out.println("successfully scheduled recurring every " + intervalMillis + " " + income.getTitle());
    }

    /**
     * Cancel the scheduled task for a specific income.
     */
    public void cancelScheduledIncomeTask(UUID incomeId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(incomeId);
        System.out.println("schedule tasks: " + scheduledTask);
        if (scheduledTask != null) {
            boolean cancelled = scheduledTask.cancel(true);
            scheduledTasks.remove(incomeId);
            System.out.println("Canceled scheduled task for income ID: " + incomeId + "was running: " + cancelled);
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
