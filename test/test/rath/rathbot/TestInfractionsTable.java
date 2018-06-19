
package test.rath.rathbot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.rath.rathbot.disc.InfractionEntry;
import com.rath.rathbot.disc.InfractionsTable;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestInfractionsTable {
  
  @BeforeClass
  public static void setup() {
    InfractionsTable.disableSaveToDisk();
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testErrors() {
    
    boolean result = InfractionsTable.initMember(-1L);
    assertFalse(result);
    
    result = InfractionsTable.hasMember(-1L);
    assertFalse(result);
    
    ArrayList<InfractionEntry> hist = InfractionsTable.getInfractionHistory(-1L);
    assertNull(hist);
    
    int intVal = InfractionsTable.getWarnCount(-1L);
    assertEquals(intVal, -1);
    
    result = InfractionsTable.warnUser(-1L, 0, "");
    assertFalse(result);
    
    result = InfractionsTable.isMuted(-1L);
    assertFalse(result);
    
    intVal = InfractionsTable.getMuteCount(-1L);
    assertEquals(intVal, -1);
    
    result = InfractionsTable.setMuted(-1L, false);
    assertFalse(result);
    
    result = InfractionsTable.muteUser(-1L, 0, 0, "");
    assertFalse(result);
    
    intVal = InfractionsTable.getKickCount(-1L);
    assertEquals(intVal, -1);
    
    result = InfractionsTable.kickUser(-1L, 0, "");
    assertFalse(result);
    
    result = InfractionsTable.isBanned(-1L);
    assertFalse(result);
    
    intVal = InfractionsTable.getBanCount(-1L);
    assertEquals(intVal, -1);
    
    result = InfractionsTable.setBanned(-1L, false);
    assertFalse(result);
    
    result = InfractionsTable.banUser(-1L, 0, "");
    assertFalse(result);
    
    result = InfractionsTable.clearInfractions(-1L);
    assertFalse(result);
    
    InfractionsTable.initTable();
    
    result = InfractionsTable.initMember(-1L);
    assertFalse(result);
    
    result = InfractionsTable.hasMember(-1L);
    assertFalse(result);
    
    hist = InfractionsTable.getInfractionHistory(-1L);
    assertNull(hist);
    
    intVal = InfractionsTable.getWarnCount(-1L);
    assertEquals(intVal, -1);
    
    result = InfractionsTable.warnUser(-1L, 0, "");
    assertFalse(result);
    
    result = InfractionsTable.isMuted(-1L);
    assertFalse(result);
    
    intVal = InfractionsTable.getMuteCount(-1L);
    assertEquals(intVal, -1);
    
    result = InfractionsTable.setMuted(-1L, false);
    assertFalse(result);
    
    result = InfractionsTable.muteUser(-1L, 0, 0, "");
    assertFalse(result);
    
    intVal = InfractionsTable.getKickCount(-1L);
    assertEquals(intVal, -1);
    
    result = InfractionsTable.kickUser(-1L, 0, "");
    assertFalse(result);
    
    result = InfractionsTable.isBanned(-1L);
    assertFalse(result);
    
    intVal = InfractionsTable.getBanCount(-1L);
    assertEquals(intVal, -1);
    
    result = InfractionsTable.setBanned(-1L, false);
    assertFalse(result);
    
    result = InfractionsTable.banUser(-1L, 0, "");
    assertFalse(result);
    
    result = InfractionsTable.clearInfractions(-1L);
    assertTrue(result);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testInit() {
    InfractionsTable.initTable();
    
    final long u = 23895789L;
    
    boolean b = InfractionsTable.hasMember(u);
    assertFalse(b);
    
    b = InfractionsTable.initMember(u);
    assertTrue(b);
    
    b = InfractionsTable.hasMember(u);
    assertTrue(b);
    
    ArrayList<InfractionEntry> l = InfractionsTable.getInfractionHistory(u);
    assertNotNull(l);
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testUpdates() {
    
    final long u = 23785623895L;
    
    boolean b = InfractionsTable.hasMember(u);
    assertFalse(b);
    
    b = InfractionsTable.initMember(u);
    assertTrue(b);
    
    int i = 0;
    // Bans
    b = InfractionsTable.banUser(u, 1000, "test");
    assertTrue(b);
    
    i = InfractionsTable.getBanCount(u);
    assertEquals(i, 1);
    
    b = InfractionsTable.banUser(u, 2000, "test2");
    assertTrue(b);
    
    i = InfractionsTable.getBanCount(u);
    assertEquals(i, 2);
    
    b = InfractionsTable.clearInfractions(u);
    assertTrue(b);
    
    i = InfractionsTable.getBanCount(u);
    assertEquals(i, 0);
    
    b = InfractionsTable.setBanned(u, true);
    assertTrue(b);
    
    i = InfractionsTable.getBanCount(u);
    assertEquals(i, 0);
    
    b = InfractionsTable.isBanned(u);
    assertTrue(b);
    
    b = InfractionsTable.setBanned(u, false);
    assertTrue(b);
    
    b = InfractionsTable.isBanned(u);
    assertFalse(b);
    
    // Kicks
    b = InfractionsTable.kickUser(u, 1000, "test");
    assertTrue(b);
    
    i = InfractionsTable.getKickCount(u);
    assertEquals(i, 1);
    
    b = InfractionsTable.kickUser(u, 2000, "test2");
    assertTrue(b);
    
    i = InfractionsTable.getKickCount(u);
    assertEquals(i, 2);
    
    b = InfractionsTable.clearInfractions(u);
    assertTrue(b);
    
    // Mutes
    b = InfractionsTable.muteUser(u, 20, 1000, "test");
    assertTrue(b);
    
    i = InfractionsTable.getMuteCount(u);
    assertEquals(i, 1);
    
    b = InfractionsTable.muteUser(u, 10, 2000, "test2");
    assertTrue(b);
    
    i = InfractionsTable.getMuteCount(u);
    assertEquals(i, 2);
    
    b = InfractionsTable.clearInfractions(u);
    assertTrue(b);
    
    i = InfractionsTable.getMuteCount(u);
    assertEquals(i, 0);
    
    b = InfractionsTable.setMuted(u, true);
    assertTrue(b);
    
    i = InfractionsTable.getMuteCount(u);
    assertEquals(i, 0);
    
    b = InfractionsTable.isMuted(u);
    assertTrue(b);
    
    b = InfractionsTable.setMuted(u, false);
    assertTrue(b);
    
    b = InfractionsTable.isMuted(u);
    assertFalse(b);
    
    // Warns
    b = InfractionsTable.warnUser(u, 1000, "test");
    assertTrue(b);
    
    i = InfractionsTable.getWarnCount(u);
    assertEquals(i, 1);
    
    b = InfractionsTable.warnUser(u, 2000, "test2");
    assertTrue(b);
    
    i = InfractionsTable.getWarnCount(u);
    assertEquals(i, 2);
    
    b = InfractionsTable.clearInfractions(u);
    assertTrue(b);
    
    i = InfractionsTable.getWarnCount(u);
    assertEquals(i, 0);
  }
  
}
