
package com.rath.rathbot.task.admin;

import com.rath.rathbot.RBConfig;
import com.rath.rathbot.task.RBTask;
import com.rath.rathbot.task.time.AbsoluteTimeConfiguration;
import com.rath.rathbot.task.time.TimeConfiguration;

public class PurgeTask extends RBTask {
  
  private static final String TASK_NAME = "purge";
  
  private static final String CONFIG_STRING = "on [Jan,Apr,Jul,Nov] 7 at 06:00";
  
  private static final TimeConfiguration TIME_CONFIG = new AbsoluteTimeConfiguration(CONFIG_STRING,
      RBConfig.getTimeZone());
  
  public PurgeTask() {
    super(TASK_NAME, TIME_CONFIG);
  }
  
  @Override
  public void performTask() {
    // TODO: performTask() in PurgeTask
  }
  
  @Override
  public boolean isResourceIntensive() {
    return true;
  }
  
  @Override
  public boolean isSystemTask() {
    return true;
  }
  
  @Override
  public long getNextEpochSecond() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public boolean doesRepeat() {
    // TODO Auto-generated method stub
    return false;
  }
  
}
