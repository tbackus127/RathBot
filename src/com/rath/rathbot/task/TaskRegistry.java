
package com.rath.rathbot.task;

import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.rath.rathbot.task.admin.PurgeWarnTask;

/**
 * This class handles all tasks registered with the bot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class TaskRegistry {
  
  /** A list of tasks to setup. */
  private static final RBTask[] taskList = { new PurgeWarnTask() };
  // TODO: ** Add more tasks here when they are implemented.
  
  /** Maps from a task's name to the RBTask handle. */
  private static final TreeMap<String, RBTask> taskMap = new TreeMap<String, RBTask>();
  
  /** The size of the thread pool. */
  private static final int THREAD_POOL_SIZE = 3;
  
  /** The task thread pool. */
  private static final ScheduledThreadPoolExecutor threadPool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(
      THREAD_POOL_SIZE);
  
  /**
   * Sets up and schedules all tasks with their default parameters.
   */
  public static final void setupTasks() {
    
    // Tasks are removed when cancelled
    threadPool.setRemoveOnCancelPolicy(true);
    
    // Iterate through all saved tasks in the task list
    for (final RBTask task : taskList) {
      
      registerTask(task.getTaskName(), task);
      
    }
    
  }
  
  /**
   * Registers a task with the given task name.
   * 
   * @param taskName the name that will be used to get a reference to the running task.
   * @param task the task that will be ran.
   * @return true if there were no errors; false if there were.
   */
  public static final boolean registerTask(final String taskName, final RBTask task) {
    
    // Ensure the task map isn't null
    if (taskMap == null) {
      System.err.println("Task map is null!");
      return false;
    }
    
    // Add the task to the task map
    taskMap.put(taskName, task);
    
    // Set up and schedule the task
    task.setupTask();
    final ScheduledFuture<?> res;
    if (task.doesRepeat()) {
      res = threadPool.scheduleAtFixedRate(task, task.getDelay(), task.getFrequency(), TimeUnit.SECONDS);
    } else {
      res = threadPool.schedule(task, task.getDelay(), TimeUnit.SECONDS);
    }
    task.setResult(res);
    
    return true;
  }
  
  /**
   * Disables the given task.
   * 
   * @param name the name of the registered task.
   */
  public static final boolean disableTask(final String name) {
    
    // Ensure the task map isn't null for some reason
    if (taskMap == null) {
      System.err.println("Task map is null!");
      return false;
    }
    
    // Get the task entry and make sure it's not also null
    final RBTask entry = taskMap.get(name);
    if (entry == null) {
      System.err.println("Task mapped by key \"" + name + "\" is null!");
      return false;
    }
    
    // Get a handle to the ScheduledFuture and cancel it
    entry.getResult().cancel(true);
    return true;
  }
  
  /**
   * Checks if the given task exists in the task set.
   * 
   * @param name the registered task name as a String.
   * @return true if the task exists; false if not, or if the task set is null.
   */
  public static final boolean taskExists(final String name) {
    
    // Ensure the task map isn't null for some reason
    if (taskMap == null) {
      System.err.println("Task map is null!");
      return false;
    }
    
    return taskMap.containsKey(name);
  }
  
}
