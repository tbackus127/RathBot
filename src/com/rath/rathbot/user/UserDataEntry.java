
package com.rath.rathbot.user;

import java.time.ZoneId;

import com.rath.rathbot.RBConfig;

public class UserDataEntry {
  
  private ZoneId timeZone;
  
  public UserDataEntry() {
    this.timeZone = RBConfig.getTimeZone();
  }
  
  public ZoneId getTimeZone() {
    return this.timeZone;
  }
  
  public void setTimeZone(final ZoneId timeZone) {
    this.timeZone = timeZone;
  }
  
}
