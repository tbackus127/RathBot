
package test.rath.rathbot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.TreeMap;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.rath.rathbot.cmd.PermissionsTable;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPermissionsTable {
  
  @BeforeClass
  public static void setup() {
    PermissionsTable.disableSaveToDisk();
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testErrors() {
    
    boolean result = PermissionsTable.initUser(0);
    assertFalse(result);
    
    result = PermissionsTable.hasUser(0);
    assertFalse(result);
    
    int lvl = PermissionsTable.getLevel(0);
    assertTrue(lvl == -1);
    
    result = PermissionsTable.removeUser(0);
    assertFalse(result);
    
    result = PermissionsTable.updateUser(0, 0);
    assertFalse(result);
    
    final TreeMap<Long, Integer> table = PermissionsTable.getPermMap();
    assertNull(table);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testInitUser() {
    
    PermissionsTable.initTable();
    
    final long uid = 8675309L;
    boolean result = PermissionsTable.hasUser(uid);
    assertFalse(result);
    
    result = PermissionsTable.initUser(uid);
    assertTrue(result);
    
    result = PermissionsTable.hasUser(uid);
  }
  
  @SuppressWarnings("static-method")
  @Test
  public void testUpdateUser() {
    
    PermissionsTable.initTable();
    
    final long uid = 4856154453L;
    int lvl = PermissionsTable.getLevel(uid);
    assertTrue(lvl == -1);
    
    boolean result = PermissionsTable.initUser(uid);
    assertTrue(result);
    
    result = PermissionsTable.updateUser(uid, 7);
    lvl = PermissionsTable.getLevel(uid);
    assertTrue(lvl == 7);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testRemoveUser() {
    
    PermissionsTable.initTable();
    final long uid = 783648392578L;
    
    boolean result = PermissionsTable.hasUser(uid);
    assertFalse(result);
    
    result = PermissionsTable.initUser(uid);
    assertTrue(result);
    
    result = PermissionsTable.hasUser(uid);
    assertTrue(result);
    
    result = PermissionsTable.updateUser(uid, 5);
    int lvl = PermissionsTable.getLevel(uid);
    assertTrue(lvl == 5);
    
    result = PermissionsTable.removeUser(uid);
    assertTrue(result);
    
    result = PermissionsTable.hasUser(uid);
    assertFalse(result);
    
  }
  
}
