
package test.rath.rathbot.task;

import java.util.ArrayList;

public class RelativeTimeTestEntry {
  
  private final String configString;
  private final ArrayList<Long> expectedTimes;
  
  public RelativeTimeTestEntry(final String configString, final ArrayList<Long> expectedTimes) {
    this.configString = configString;
    this.expectedTimes = expectedTimes;
  }
  
  public String getConfigString() {
    return this.configString;
  }
  
  public ArrayList<Long> getExpectedTimes() {
    return this.expectedTimes;
  }
  
}
