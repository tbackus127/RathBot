
package com.rath.rathbot.task;

import java.util.concurrent.ScheduledFuture;

import com.rath.rathbot.task.time.TimeConfiguration;

/**
 * This class acts as a skeleton for periodic/delayed tasks performed by the bot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public abstract class RBTask implements Runnable {
  
  /** The name of the task. */
  protected final String taskName;
  
  /** The timestring this task's execution follows. */
  protected final TimeConfiguration timeConfig;
  
  /** The result of the task, for cancelling. */
  protected ScheduledFuture<?> result;
  
  /**
   * Constructs a new RBTask object.
   * 
   * @param name the name of the task as a String.
   * @param timeConfig a time configuration that contains the temporal data of this task.
   */
  protected RBTask(final String name, final TimeConfiguration timeConfig) {
    this.taskName = name;
    this.timeConfig = timeConfig;
  }
  
  /**
   * Gets the name of the task.
   * 
   * @return the task name that is used for task mapping as a String.
   */
  public final String getTaskName() {
    return this.taskName;
  }
  
  /**
   * Sets the result of this task so it can be cancelled.
   * 
   * @param fut the ScheduledFuture that this task returns.
   */
  public final void setResult(final ScheduledFuture<?> fut) {
    this.result = fut;
  }
  
  /**
   * Gets the task result so it can be cancelled.
   * 
   * @return the ScheduledFuture that this task returns.
   */
  public final ScheduledFuture<?> getResult() {
    return this.result;
  }
  
  /**
   * Returns the next epoch second this task will execute at.
   * 
   * @return a long.
   */
  public abstract long getNextEpochSecond();
  
  /**
   * Checks if the task is repeated or not.
   * 
   * @return true if the task will be immediately rescheduled after it finishes; false if not.
   */
  public abstract boolean doesRepeat();
  
  /**
   * Runs the task.
   */
  @Override
  public final void run() {
    setupTask();
    performTask();
    TaskRegistry.unloadTask(this.getTaskName());
    checkReschedule();
  }
  
  /**
   * Called before scheduling. Override if needed.
   */
  @SuppressWarnings("static-method")
  public void setupTask() {
    return;
  }
  
  /**
   * Whether this task should be automatically scheduled at startup.
   * 
   * @return true if the task should be immediately scheduled; false if not.
   */
  @SuppressWarnings("static-method")
  public boolean isSystemTask() {
    return false;
  }
  
  /**
   * Has the task do its thing.
   */
  public abstract void performTask();
  
  /**
   * Whether this task is resource intensive, and thus requires putting the bot in a low-workload state.
   * 
   * @return true if this task uses a lot of resources; false if not.
   */
  public abstract boolean isResourceIntensive();
  
  /**
   * Reschedules this task if it should repeat.
   */
  private final void checkReschedule() {
    if (this.doesRepeat()) {
      TaskRegistry.loadTask(this);
    }
  }
}
