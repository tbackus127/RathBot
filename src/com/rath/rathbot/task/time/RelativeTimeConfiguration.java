
package com.rath.rathbot.task.time;

public class RelativeTimeConfiguration extends TimeConfiguration {
  
  private final boolean doesRepeat;
  
  public RelativeTimeConfiguration(final String timeString, final boolean doesRepeat) {
    super(TimeConfigurationType.RELATIVE, timeString);
    this.doesRepeat = doesRepeat;
  }
  
  @Override
  public long getNextEpochTime() {
    
    // TODO: Implement getNextEpochTime() in RelativeTimeConfiguration
    return 0;
  }
  
  @Override
  public final boolean doesRepeat() {
    return this.doesRepeat;
  }
  
}
