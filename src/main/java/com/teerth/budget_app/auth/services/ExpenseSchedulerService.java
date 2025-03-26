package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ExpenseSchedulerService {
    @Autowired
    private TaskScheduler taskScheduler;


    private final Map<UUID, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * Schedule recurring expense addition.
     */
    public void schedulerExpenseAddition(Expense expense, String interval, Runnable task) {
//        String interval = expense.getInterval();
        System.out.println("interval inside scheduler: " + interval);
//        if (interval != null && !interval.isEmpty()) {
//            long intervalMillis = convertIntervalToMillis(interval);
//
//            // Schedule the task
//            ScheduledFuture<?> scheduledTask = taskScheduler.scheduleAtFixedRate(task, intervalMillis);
//
//            // Store the scheduled task for later cancellation
//            scheduledTasks.put(expense.getExpense_id(), scheduledTask);
//
//            System.out.println("Scheduled recurring addition every " + intervalMillis + " ms for expense: " + expense.getTitle());
//        } else {
//            System.out.println("Interval is null or empty, no task scheduled.");
//        }

        long intervalMillis;
        try{
            intervalMillis = convertIntervalToMillis(interval);
            System.out.println("converting interval into millis: " + intervalMillis);
        }catch (Exception e){
            System.out.println("Error converting interval: " + e.getMessage());
            return;
        }

        System.out.println("taskscheduler instance: " + taskScheduler);
        System.out.println("before schedule task: ");
        PeriodicTrigger trigger = new PeriodicTrigger(intervalMillis, TimeUnit.MILLISECONDS);
        trigger.setInitialDelay(intervalMillis);
//        ScheduledFuture<?> scheduledTask = taskScheduler.scheduleAtFixedRate(task,intervalMillis);
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, trigger);
        System.out.println("after schedule task: " + scheduledTask);

        if(scheduledTask == null){
            System.out.println("Error: task scheduler return null");
            return;
        }
        System.out.println("id before mapping: " + expense.getExpense_id());
        scheduledTasks.put(expense.getExpense_id(),scheduledTask);
        System.out.println("schedule tasks mapping: " + scheduledTasks);
        System.out.println("successfully scheduled recurring every " + intervalMillis + " " + expense.getTitle());
    }

    /**
     * Cancel the scheduled task for a specific expense.
     */
    public void cancelScheduledExpenseTask(UUID expenseId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(expenseId);
        System.out.println("schedule task from cancel func.." + scheduledTask);

        if (scheduledTask != null) {
            boolean cancelled = scheduledTask.cancel(true);
            // Cancel without interrupting if running
            scheduledTasks.remove(expenseId);
            System.out.println("Canceled scheduled task for expense ID: " + expenseId + "was running: " + cancelled);
        } else {
            System.out.println("No scheduled task found for expense ID: " + expenseId);
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
}
