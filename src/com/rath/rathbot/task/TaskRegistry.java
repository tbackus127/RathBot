
package com.rath.rathbot.task;

import java.util.HashMap;

/**
 * This class handles all tasks registered with the bot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class TaskRegistry {
  
  /** The set of tasks. */
  private static final HashMap<String, RBTask> taskSet = new HashMap<String, RBTask>();
  
  /**
   * Register a task for the bot.
   * 
   * @param task the task as a RBTask object.
   */
  public static final void registerTask(final String name, final RBTask task) {
    taskSet.put(name, task);
  }
  
  /**
   * Gets all tasks currently registered.
   * 
   * @return a HashMap from String -> RBTask.
   */
  public static final HashMap<String, RBTask> getTaskMap() {
    return taskSet;
  }
  
  /**
   * Gets a task by its name.
   * 
   * @param name the task's registered name.
   * @return
   */
  public static final RBTask getTaskByName(final String name) {
    return taskSet.get(name);
  }
}
