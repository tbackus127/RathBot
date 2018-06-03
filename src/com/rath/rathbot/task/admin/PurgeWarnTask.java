
package com.rath.rathbot.task.admin;

import com.rath.rathbot.task.RBTask;

public class PurgeWarnTask extends RBTask {
  
  private static final String TASK_NAME = "purgewarn";
  
  // TODO: Have this use the Date object and recur every 1st of every 3rd month, purging every 8th of every third.
  private static final int TASK_FREQUENCY = 7776000;
  
  public PurgeWarnTask() {
    super(TASK_NAME, TASK_FREQUENCY, 0, RBTask.REPEAT);
  }
  
  @Override
  public void setupTask() {
    
  }
  
  @Override
  public void run() {
    // TODO: Implement task 'purgewarn'
  }
  
}
