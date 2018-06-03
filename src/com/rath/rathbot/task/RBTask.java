
package com.rath.rathbot.task;

import java.util.concurrent.ScheduledFuture;

/**
 * This class acts as a skeleton for periodic/delayed tasks performed by the bot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public abstract class RBTask implements Runnable {
  
  /** Constant for if the task repeats. */
  public static final boolean REPEAT = true;
  
  /** Constant for if the task does not repeat. */
  public static final boolean NO_REPEAT = false;
  
  /** The name of the task. */
  protected final String taskName;
  
  /** If the task repeats or not. */
  protected final boolean doesTaskRepeat;
  
  /** The execution frequency, in seconds. */
  protected long execFrequencySeconds;
  
  /** The delay before executing the task, in seconds. */
  protected final long execDelaySeconds;
  
  /** The result of the task, for cancelling. */
  protected ScheduledFuture<?> result;
  
  protected RBTask(final String name, final long execFrequencySeconds, final long execDelay,
      final boolean doesTaskRepeat) {
    this.taskName = name;
    this.execFrequencySeconds = execFrequencySeconds;
    this.execDelaySeconds = execDelay;
    this.doesTaskRepeat = doesTaskRepeat;
  }
  
  public final String getTaskName() {
    return this.taskName;
  }
  
  public final boolean doesRepeat() {
    return this.doesTaskRepeat;
  }
  
  public final void setFrequency(final int freq) {
    this.execFrequencySeconds = freq;
  }
  
  public final long getFrequency() {
    return this.execFrequencySeconds;
  }
  
  public final long getDelay() {
    return this.execDelaySeconds;
  }
  
  public final void setResult(final ScheduledFuture<?> fut) {
    this.result = fut;
  }
  
  public final ScheduledFuture<?> getResult() {
    return this.result;
  }
  
  @SuppressWarnings("static-method")
  public void setupTask() {
    return;
  }
  
}
