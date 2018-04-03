
package test.rath.rathbot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.TreeMap;

import org.junit.Test;

import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.cmd.admin.UIDCmd;
import com.rath.rathbot.cmd.disc.actions.MuteCmd;
import com.rath.rathbot.cmd.msg.faq.FAQCmd;
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
    
    String[] strArr = { "a", "b", "c" };
    String res = MessageHelper.concatenateTokens(strArr, 3);
    assertNull(res);
    
    res = MessageHelper.concatenateTokens(strArr, 0);
    assertEquals("a b c", res);
    
    res = MessageHelper.concatenateTokens(strArr, 1);
    assertEquals("b c", res);
    
    res = MessageHelper.concatenateTokens(strArr, 2);
    assertEquals("c", res);
    
  }
  
  @Test
  public void testListString() {
    fail("Write this.");
  }
  
  @Test
  public void testDiscNotification() {
    fail("Write this.");
  }
  
}