
package com.rath.rathbot.task.time;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Iterator;

public class RelativeTimeConfiguration extends TimeConfiguration {
  
  private final long scheduleTime;
  
  public RelativeTimeConfiguration(final String timeString, final ZoneId testZoneId) {
    super(TimeConfigurationType.RELATIVE, timeString);
    this.scheduleTime = Instant.now().getEpochSecond();
  }
  
  @Override
  public Iterator<Long> iterator() {
    return new Iterator<Long>() {
      
      @Override
      public boolean hasNext() {
        // TODO Auto-generated method stub
        return false;
      }
      
      @Override
      public Long next() {
        // TODO Auto-generated method stub
        return null;
      }
      
    };
  }
}