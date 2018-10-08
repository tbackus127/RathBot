
package test.rath.rathbot.task;

import java.util.ArrayList;

public class AbsoluteTimeTestEntry {
  
  private final String configString;
  private final ArrayList<Long> expected;
  
  public AbsoluteTimeTestEntry(final String config, ArrayList<Long> exp) {
    this.configString = config;
    this.expected = exp;
  }
  
  public String getConfigString() {
    return this.configString;
  }
  
  public ArrayList<Long> getExpected() {
    return this.expected;
  }
}
