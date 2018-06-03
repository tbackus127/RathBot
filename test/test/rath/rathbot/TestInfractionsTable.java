
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
import com.rath.rathbot.disc.Infractions;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestInfractionsTable {
  
  @BeforeClass
  public static void setup() {
    Infractions.disableSaveToDisk();
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testErrors() {
    
    boolean result = Infractions.initMember(-1L);
    assertFalse(result);
    
    result = Infractions.hasMember(-1L);
    assertFalse(result);
    
    ArrayList<InfractionEntry> hist = Infractions.getInfractionHistory(-1L);
    assertNull(hist);
    
    int intVal = Infractions.getWarnCount(-1L);
    assertEquals(intVal, -1);
    
    result = Infractions.warnUser(-1L, 0, "");
    assertFalse(result);
    
    result = Infractions.isMuted(-1L);
    assertFalse(result);
    
    intVal = Infractions.getMuteCount(-1L);
    assertEquals(intVal, -1);
    
    result = Infractions.setMuted(-1L, false);
    assertFalse(result);
    
    result = Infractions.muteUser(-1L, 0, 0, "");
    assertFalse(result);
    
    intVal = Infractions.getKickCount(-1L);
    assertEquals(intVal, -1);
    
    result = Infractions.kickUser(-1L, 0, "");
    assertFalse(result);
    
    result = Infractions.isBanned(-1L);
    assertFalse(result);
    
    intVal = Infractions.getBanCount(-1L);
    assertEquals(intVal, -1);
    
    result = Infractions.setBanned(-1L, false);
    assertFalse(result);
    
    result = Infractions.banUser(-1L, 0, "");
    assertFalse(result);
    
    result = Infractions.clearInfractions(-1L);
    assertFalse(result);
    
    Infractions.initTable();
    
    result = Infractions.initMember(-1L);
    assertFalse(result);
    
    result = Infractions.hasMember(-1L);
    assertFalse(result);
    
    hist = Infractions.getInfractionHistory(-1L);
    assertNull(hist);
    
    intVal = Infractions.getWarnCount(-1L);
    assertEquals(intVal, -1);
    
    result = Infractions.warnUser(-1L, 0, "");
    assertFalse(result);
    
    result = Infractions.isMuted(-1L);
    assertFalse(result);
    
    intVal = Infractions.getMuteCount(-1L);
    assertEquals(intVal, -1);
    
    result = Infractions.setMuted(-1L, false);
    assertFalse(result);
    
    result = Infractions.muteUser(-1L, 0, 0, "");
    assertFalse(result);
    
    intVal = Infractions.getKickCount(-1L);
    assertEquals(intVal, -1);
    
    result = Infractions.kickUser(-1L, 0, "");
    assertFalse(result);
    
    result = Infractions.isBanned(-1L);
    assertFalse(result);
    
    intVal = Infractions.getBanCount(-1L);
    assertEquals(intVal, -1);
    
    result = Infractions.setBanned(-1L, false);
    assertFalse(result);
    
    result = Infractions.banUser(-1L, 0, "");
    assertFalse(result);
    
    result = Infractions.clearInfractions(-1L);
    assertTrue(result);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testInit() {
    Infractions.initTable();
    
    final long u = 23895789L;
    
    boolean b = Infractions.hasMember(u);
    assertFalse(b);
    
    b = Infractions.initMember(u);
    assertTrue(b);
    
    b = Infractions.hasMember(u);
    assertTrue(b);
    
    ArrayList<InfractionEntry> l = Infractions.getInfractionHistory(u);
    assertNotNull(l);
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testUpdates() {
    
    final long u = 23785623895L;
    
    boolean b = Infractions.hasMember(u);
    assertFalse(b);
    
    b = Infractions.initMember(u);
    assertTrue(b);
    
    int i = 0;
    // Bans
    b = Infractions.banUser(u, 1000, "test");
    assertTrue(b);
    
    i = Infractions.getBanCount(u);
    assertEquals(i, 1);
    
    b = Infractions.banUser(u, 2000, "test2");
    assertTrue(b);
    
    i = Infractions.getBanCount(u);
    assertEquals(i, 2);
    
    b = Infractions.clearInfractions(u);
    assertTrue(b);
    
    i = Infractions.getBanCount(u);
    assertEquals(i, 0);
    
    b = Infractions.setBanned(u, true);
    assertTrue(b);
    
    i = Infractions.getBanCount(u);
    assertEquals(i, 0);
    
    b = Infractions.isBanned(u);
    assertTrue(b);
    
    b = Infractions.setBanned(u, false);
    assertTrue(b);
    
    b = Infractions.isBanned(u);
    assertFalse(b);
    
    // Kicks
    b = Infractions.kickUser(u, 1000, "test");
    assertTrue(b);
    
    i = Infractions.getKickCount(u);
    assertEquals(i, 1);
    
    b = Infractions.kickUser(u, 2000, "test2");
    assertTrue(b);
    
    i = Infractions.getKickCount(u);
    assertEquals(i, 2);
    
    b = Infractions.clearInfractions(u);
    assertTrue(b);
    
    // Mutes
    b = Infractions.muteUser(u, 20, 1000, "test");
    assertTrue(b);
    
    i = Infractions.getMuteCount(u);
    assertEquals(i, 1);
    
    b = Infractions.muteUser(u, 10, 2000, "test2");
    assertTrue(b);
    
    i = Infractions.getMuteCount(u);
    assertEquals(i, 2);
    
    b = Infractions.clearInfractions(u);
    assertTrue(b);
    
    i = Infractions.getMuteCount(u);
    assertEquals(i, 0);
    
    b = Infractions.setMuted(u, true);
    assertTrue(b);
    
    i = Infractions.getMuteCount(u);
    assertEquals(i, 0);
    
    b = Infractions.isMuted(u);
    assertTrue(b);
    
    b = Infractions.setMuted(u, false);
    assertTrue(b);
    
    b = Infractions.isMuted(u);
    assertFalse(b);
    
    // Warns
    b = Infractions.warnUser(u, 1000, "test");
    assertTrue(b);
    
    i = Infractions.getWarnCount(u);
    assertEquals(i, 1);
    
    b = Infractions.warnUser(u, 2000, "test2");
    assertTrue(b);
    
    i = Infractions.getWarnCount(u);
    assertEquals(i, 2);
    
    b = Infractions.clearInfractions(u);
    assertTrue(b);
    
    i = Infractions.getWarnCount(u);
    assertEquals(i, 0);
  }
  
}
