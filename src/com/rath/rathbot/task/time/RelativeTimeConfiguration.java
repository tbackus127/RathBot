
package com.rath.rathbot.task.time;

import java.time.Instant;

public class RelativeTimeConfiguration extends TimeConfiguration {
  
  private final long scheduleTime;
  
  public RelativeTimeConfiguration(final String timeString) {
    super(TimeConfigurationType.RELATIVE, timeString);
    this.scheduleTime = Instant.now().getEpochSecond();
  }
  
  @Override
  public long getNextEpochTime() {
    
    long result = 0;
    
    // TODO: getNextEpochTime() in RelativeTimeConfiguration
    
    return result;
  }
  
  public long getScheduleTime() {
    return this.scheduleTime;
  }
  
  @Override
  public boolean doesRepeat() {
    return this.config.startsWith("every");
  }
  
}
