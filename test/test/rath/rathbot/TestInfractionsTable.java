
package test.rath.rathbot;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import com.rath.rathbot.disc.Infractions;

public class TestInfractionsTable {
  
  @BeforeClass
  public static void setup() {
    Infractions.disableSaveToDisk();
  }
  
  @Test
  public void test() {
    fail("Not yet implemented");
  }
  
}
