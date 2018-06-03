
package test.rath.rathbot.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

import com.rath.rathbot.util.TimeHelper;

public class TestTimeHelper {
  
  //@formatter:off
  /*
   * seconds  1         s
   * minutes  60        m
   * hours    3600      h
   * days     86400     d
   * weeks    604800    w
   * months   2592000   M
   * 
   * Limit: 31557600 (1 year, converted from 365.25 days)
   * 
   */
  //@formatter:on
  
  private static final String[] invalidStrings = { "", " ", "1", "0", "-1", "f", "1f", "1S", "0s", "as", "12M1s", "" };
  private static final int expectedErrorCount = invalidStrings.length;
  
  private static final HashMap<String, Long> testMap = new HashMap<String, Long>() {
    
    private static final long serialVersionUID = 1L;
    {
      put("1s", 1L);
      put("32s", 32L);
      put("800s", 800L);
      put("1m", 60L);
      put("14m", 840L);
      put("1m14s", 74L);
      put("8m359s", 839L);
      put("1h", 3600L);
      put("7h", 25200L);
      put("2h14s", 7214L);
      put("2h118m", 14280L);
      put("4h30m11s", 16211L);
    }
  };
  
  @Test
  @SuppressWarnings("static-method")
  public void testInvalidTimestrings() {
    
    int errCount = 0;
    for (int i = 0; i < invalidStrings.length; i++) {
      final long result = TimeHelper.timestringToEpochSecond(invalidStrings[i]);
      if (result == -1) {
        errCount++;
      } else {
        System.err.println("  Test string \"" + invalidStrings[i] + "\" should have returned -1!");
      }
    }
    
    assertEquals(errCount, expectedErrorCount);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testValidTimestrings() {
    fail("Not yet implemented.");
  }
  
}
