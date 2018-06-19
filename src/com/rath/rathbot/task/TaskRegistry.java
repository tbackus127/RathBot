
package com.rath.rathbot.task;

import java.io.File;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.task.admin.PurgeTask;
import com.rath.rathbot.task.admin.PurgeWarnTask;
import com.rath.rathbot.task.system.SaveTaskInfoTask;

/**
 * This class handles all tasks registered with the bot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class TaskRegistry {
  
  /** The relative directory from the bot's data folder where tasks will be stored. */
  private static final String DIR_TASKS = "tasks/";
  
  /** The full tasks path from the bot's root directory. */
  private static final String PATH_TASKS = RathBot.DIR_DATA + DIR_TASKS;
  
  /** The handle to the bot's task directory as an open File object. */
  private static final File FILE_TASKS = new File(PATH_TASKS);
  
  /** A list of tasks that start automatically with RathBot. */
  private static final RBTask[] systemTasks = { new PurgeWarnTask(), new PurgeTask(), new SaveTaskInfoTask() };
  // TODO: ** Add more tasks here when they are implemented.
  
  /** Maps from a task's name to the RBTask handle. */
  private static final TreeMap<String, RBTask> taskMap = new TreeMap<String, RBTask>();
  
  /** Tasks that are not system tasks and were added via a command. */
  private static final TreeSet<RBTask> tempTasks = new TreeSet<RBTask>();
  
  /** The size of the thread pool. */
  private static final int THREAD_POOL_SIZE = 3;
  
  /** The task thread pool. */
  private static final ScheduledThreadPoolExecutor threadPool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(
      THREAD_POOL_SIZE);
  
  /**
   * Sets up and schedules all tasks with their default parameters.
   */
  public static final void startup() {
    
    // Load system tasks
    for (int i = 0; i < systemTasks.length; i++) {
      loadTask(systemTasks[i]);
    }
    
    // Load temp tasks from disk
    loadTempTasksFromDisk();
    for (final RBTask tempTask : tempTasks) {
      loadTask(tempTask);
    }
    
    // Tasks are removed when cancelled
    threadPool.setRemoveOnCancelPolicy(true);
    
  }
  
  /**
   * Registers a task with the given task name.
   * 
   * @param task the task that will be ran.
   * @return true if there were no errors; false if there were.
   */
  public static final boolean loadTask(final RBTask task) {
    
    // Ensure the task map isn't null for some reason
    if (taskMap == null) {
      System.err.println("Task map is null!");
      return false;
    }
    
    // Check that the task isn't already scheduled.
    final String taskName = task.getTaskName();
    if (taskMap.containsKey(taskName)) {
      System.err.println("The task \"" + taskName + "\" is already scheduled.");
    }
    
    threadPool.schedule(task, task.getNextEpochSecond(), TimeUnit.SECONDS);
    return true;
  }
  
  /**
   * Stops and unloads the given task.
   * 
   * @param name the name of the registered task.
   */
  public static final boolean unloadTask(final String name) {
    
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
  
  /**
   * Saves all non-system tasks to the disk.
   */
  public static final void saveTempTasksToDisk() {
    
    for(final RBTask task : tempTasks) {
      
    }
    
  }
  
  /**
   * Loads all temporary tasks from storage.
   */
  private static final void loadTempTasksFromDisk() {
    // TODO loadTempTasksFromDisk() in TaskRegistry.java
  }
}
