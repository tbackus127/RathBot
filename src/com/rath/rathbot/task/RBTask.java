
package com.rath.rathbot.task;

public abstract class RBTask implements Runnable {
  
  protected final String taskName;
  protected boolean performTask;
  protected int taskFrequencySeconds;
  
  protected RBTask(final String name, final int freq) {
    this.taskName = name;
    this.taskFrequencySeconds = freq;
    TaskRegistry.registerTask(this.taskName, this);
  }
  
  public final void setTaskFrequency(final int seconds) {
    this.taskFrequencySeconds = seconds;
  }
  
  public final void enableTask() {
    this.performTask = true;
  }
  
  public final void disableTask() {
    this.performTask = false;
  }
  
  public abstract void performTask();
  
}
