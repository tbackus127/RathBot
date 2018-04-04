
package test.rath.rathbot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.Test;

import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.cmd.admin.UIDCmd;
import com.rath.rathbot.cmd.disc.actions.MuteCmd;
import com.rath.rathbot.cmd.msg.faq.FAQCmd;
import com.rath.rathbot.disc.PunishmentType;
import com.rath.rathbot.msg.MessageHelper;

public class TestMessageHelper {
  
  @Test
  public void testCommandDescription() {
    
    String result = MessageHelper.buildCmdDescrMsg(null, null);
    String expected = MessageHelper.NO_COMMANDS_MSG;
    assertEquals(result, expected);
    
    result = MessageHelper.buildCmdDescrMsg("", null);
    assertEquals(result, expected);
    
    result = MessageHelper.buildCmdDescrMsg("Header", null);
    assertEquals(result, expected);
    
    final FAQCmd fc = new FAQCmd();
    final UIDCmd uc = new UIDCmd();
    final MuteCmd mc = new MuteCmd();
    
    // Make sure the commands are in order (TreeMap sorts traversals)
    expected = fc.getCommandName() + " - " + fc.getCommandDescription() + "\n" + mc.getCommandName() + " - "
        + mc.getCommandDescription() + "\n" + uc.getCommandName() + " - " + uc.getCommandDescription();
    
    TreeMap<String, RBCommand> map = new TreeMap<String, RBCommand>();
    map.put("faq", new FAQCmd());
    map.put("uid", new UIDCmd());
    map.put("mute", new MuteCmd());
    
    result = MessageHelper.buildCmdDescrMsg(null, map);
    assertEquals(result, expected);
    
    result = MessageHelper.buildCmdDescrMsg("", map);
    assertEquals(result, expected);
    
    result = MessageHelper.buildCmdDescrMsg("Header", map);
    expected = "Header\n  " + fc.getCommandName() + " - " + fc.getCommandDescription() + "\n  " + mc.getCommandName()
        + " - " + mc.getCommandDescription() + "\n  " + uc.getCommandName() + " - " + uc.getCommandDescription();
    assertEquals(result, expected);
    
  }
  
  @Test
  public void testConcatTokens() {
    
    String[] nullArray = null;
    String res = MessageHelper.concatenateTokens(nullArray, 0);
    assertNull(res);
    
    final String[] strArr = { "a", "b", "c" };
    res = MessageHelper.concatenateTokens(strArr, 3);
    assertNull(res);
    
    res = MessageHelper.concatenateTokens(strArr, 0);
    assertEquals("a b c", res);
    
    res = MessageHelper.concatenateTokens(strArr, 1);
    assertEquals("b c", res);
    
    res = MessageHelper.concatenateTokens(strArr, 2);
    assertEquals("c", res);
    
    final String[] strArr2 = { "a" };
    res = MessageHelper.concatenateTokens(strArr2, 0);
    assertEquals("a", res);
    
  }
  
  @Test
  public void testListString() {
    
    String result = null;
    String exp = "";
    
    result = MessageHelper.buildListString(null, null, false);
    assertNull(result);
    
    result = MessageHelper.buildListString(null, null, true);
    assertNull(result);
    
    result = MessageHelper.buildListString("", null, false);
    assertNull(result);
    
    result = MessageHelper.buildListString("", null, true);
    assertNull(result);
    
    final ArrayList<String> list = new ArrayList<String>();
    result = MessageHelper.buildListString(null, list, false);
    exp = "List is empty.";
    assertEquals(exp, result);
    
    result = MessageHelper.buildListString(null, list, true);
    exp = "List is empty.";
    assertEquals(exp, result);
    
    result = MessageHelper.buildListString("Header", list, false);
    exp = "Header\n  List is empty.";
    assertEquals(exp, result);
    
    result = MessageHelper.buildListString("Header", list, true);
    exp = "Header\n  List is empty.";
    assertEquals(exp, result);
    
    list.add("foo");
    list.add("bar");
    list.add("baz");
    
    result = MessageHelper.buildListString(null, list, false);
    exp = "foo, bar, baz";
    assertEquals(exp, result);
    
    result = MessageHelper.buildListString("", list, false);
    exp = "foo, bar, baz";
    assertEquals(exp, result);
    
    result = MessageHelper.buildListString(null, list, true);
    exp = "foo\n  bar\n  baz";
    assertEquals(exp, result);
    
    result = MessageHelper.buildListString("", list, true);
    exp = "foo\n  bar\n  baz";
    assertEquals(exp, result);
    
    result = MessageHelper.buildListString("Header", list, false);
    exp = "Header\n  foo, bar, baz";
    assertEquals(exp, result);
    
    result = MessageHelper.buildListString("Header", list, true);
    exp = "Header\n  foo\n  bar\n  baz";
    assertEquals(exp, result);
    
  }
  
  @Test
  public void testDiscNotification() {
    String exp = "";
    String res = "";
    
    res = MessageHelper.buildDiscNotificationMessage(null, -1345, null);
    assertNull(res);
    
    res = MessageHelper.buildDiscNotificationMessage(null, -1345, "Test reason.");
    assertNull(res);
    
    res = MessageHelper.buildDiscNotificationMessage(null, 0, null);
    assertNull(res);
    
    res = MessageHelper.buildDiscNotificationMessage(null, 0, "Test reason.");
    assertNull(res);
    
    res = MessageHelper.buildDiscNotificationMessage(null, 53714, null);
    assertNull(res);
    
    res = MessageHelper.buildDiscNotificationMessage(null, 53714, "Test reason.");
    assertNull(res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.BAN, -1, "Test reason.");
    exp = "You have been banned from the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.BAN, 0, "Test reason.");
    exp = "You have been banned from the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.BAN, 6854, "Test reason.");
    exp = "You have been banned from the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.KICK, -1, "Test reason.");
    exp = "You have been kicked from the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.KICK, 0, "Test reason.");
    exp = "You have been kicked from the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.KICK, 6854, "Test reason.");
    exp = "You have been kicked from the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.WARN, -1, "Test reason.");
    exp = "You have been warned in the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.WARN, 0, "Test reason.");
    exp = "You have been warned in the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.WARN, 6854, "Test reason.");
    exp = "You have been warned in the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, -1, "Test reason.");
    exp = "You have been muted in the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 0, "Test reason.");
    exp = "You have been muted in the osu! University server.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, -1, null);
    exp = "You have been muted in the osu! University server.\n" + "Reason: \"No reason provided.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, -1, "");
    exp = "You have been muted in the osu! University server.\n" + "Reason: \"No reason provided.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 0, null);
    exp = "You have been muted in the osu! University server.\n" + "Reason: \"No reason provided.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 0, "");
    exp = "You have been muted in the osu! University server.\n" + "Reason: \"No reason provided.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
  }
  
  @Test
  public void testDiscNotificationTimes() {
    
    String res = "";
    String exp = "";
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 30, "Test reason.");
    exp = "You have been muted in the osu! University server for 30s.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 60, "Test reason.");
    exp = "You have been muted in the osu! University server for 1m.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 181, "Test reason.");
    exp = "You have been muted in the osu! University server for 3m1s.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 3600, "Test reason.");
    exp = "You have been muted in the osu! University server for 1h.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 3720, "Test reason.");
    exp = "You have been muted in the osu! University server for 1h2m.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 3753, "Test reason.");
    exp = "You have been muted in the osu! University server for 1h2m33s.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 86399, "Test reason.");
    exp = "You have been muted in the osu! University server for 23h59m59s.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 86400, "Test reason.");
    exp = "You have been muted in the osu! University server for 1d.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 90000, "Test reason.");
    exp = "You have been muted in the osu! University server for 1d1h.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 86413, "Test reason.");
    exp = "You have been muted in the osu! University server for 1d13s.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
    
    res = MessageHelper.buildDiscNotificationMessage(PunishmentType.MUTE, 5492473, "Test reason.");
    exp = "You have been muted in the osu! University server for 63d13h41m13s.\n" + "Reason: \"Test reason.\".\n"
        + MessageHelper.ERROR_CONTACT_MSG;
    assertEquals(exp, res);
  }
}