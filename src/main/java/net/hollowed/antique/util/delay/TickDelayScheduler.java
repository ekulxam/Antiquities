package net.hollowed.antique.util.delay;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TickDelayScheduler {

    private static final Map<Integer, DelayHandler> scheduledTasks = new ConcurrentHashMap<>();
    private static int taskIdCounter = 0;

    /**
     * Uses a Runnable to schedule code to run after a set amount of ticks
     *
     * @param ticks Delay before execution
     * @param task Code executed after the delay
     * @return the task ID in the case of a cancel being warranted
     */
    @SuppressWarnings("unused")
    public static int schedule(int ticks, Runnable task) {
        int taskId = taskIdCounter++;

        scheduledTasks.put(taskId, new DelayHandler(ticks, task, taskId));
        return taskId;
    }

    /**
     * Just a tick method, called at the end of every server tick.
     */
    public static void tick() {
        if (!scheduledTasks.isEmpty()) {
            for (int i = 0; i < scheduledTasks.size(); i++) {
                DelayHandler handler = scheduledTasks.values().stream().toList().get(i);
                if (handler.ticks > 0) {
                    handler.ticks--;
                } else {
                    handler.task.run();
                    scheduledTasks.remove(handler.id);
                    if (scheduledTasks.isEmpty()) break;
                }
            }
        }
    }

    /**
     * Cancels a scheduled task by ID.
     *
     * @param taskId ID of the task.
     */
    public static void cancel(int taskId) {
        scheduledTasks.remove(taskId);
    }
}
