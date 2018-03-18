
package com.rath.rathbot.data;

public class InfractionData {
  
  private int warnCount;
  private int kickCount;
  
  public InfractionData() {
    setWarnCount(0);
    setKickCount(0);
  }
  
  public int getWarnCount() {
    return warnCount;
  }
  
  public void setWarnCount(int warnCount) {
    this.warnCount = warnCount;
  }
  
  public int getKickCount() {
    return kickCount;
  }
  
  public void setKickCount(int kickCount) {
    this.kickCount = kickCount;
  }
}
