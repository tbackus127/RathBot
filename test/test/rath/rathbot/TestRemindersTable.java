
package test.rath.rathbot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.rath.rathbot.task.util.remindme.Reminders;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRemindersTable {
  
  @BeforeClass
  public static void setup() {
    Reminders.disableSaveToDisk();
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testErrors() {
    
    // Test initializing a user with a negative UID
    boolean result = Reminders.initUser(-1L);
    assertFalse(result);
    
    
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testUpdates() {
    
    final Long uid = 8675309L;
    
    boolean result = Reminders.initUser(uid);
    assertTrue(result);
    
  }
  
}
