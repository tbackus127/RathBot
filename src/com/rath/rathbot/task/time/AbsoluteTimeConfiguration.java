
package com.rath.rathbot.task.time;

import java.time.ZoneId;

public class AbsoluteTimeConfiguration extends TimeConfiguration {
  
  private final ZoneId timeZone;
  
  public AbsoluteTimeConfiguration(final String configString, final ZoneId zoneID) {
    super(TimeConfigurationType.ABSOLUTE, configString);
    this.timeZone = zoneID;
  }
  
  @Override
  public long getNextEpochTime() {
    
    // TODO: getNextEpochTime() in AbsoluteTimeConfiguration.java
    return -1L;
    
  }
  
  public ZoneId getTimeZone() {
    return this.timeZone;
  }
  
}
