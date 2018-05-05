
package test.rath.rathbot.cmd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.rath.rathbot.cmd.msg.faq.FAQCmd;
import com.rath.rathbot.exceptions.FAQNotFoundException;

public class TestFAQCmd {
  
  @BeforeClass
  public static void setupFAQTest() {
    System.out.println("Disabling saving FAQ map to disk...");
    FAQCmd.disableSaveToDisk();
    final FAQCmd fq = new FAQCmd();
    fq.setupCommand();
  }
  
  @Test
  public void testFAQAdd() {
    
    System.out.println("Testing FAQ.add");
    FAQCmd.addFaq("testa", "test-a-msg");
    FAQCmd.addFaq("testb", "test-b-msg");
    FAQCmd.addFaq("testc", "test-c-msg");
    
    int count = 0;
    if (FAQCmd.hasFaq("testa")) count++;
    if (FAQCmd.hasFaq("testb")) count++;
    if (FAQCmd.hasFaq("testc")) count++;
    
    assertTrue(count == 3);
  }
  
  @Test
  public void testFAQHas() {
    
    FAQCmd.addFaq("testa", "test-a-msg");
    assertTrue(FAQCmd.hasFaq("testa"));
  }
  
  @Test
  public void testFAQGet() {
    
    final String key1 = "key1";
    final String val1 = "val1";
    final String key2 = "key2";
    final String val2 = "val2";
    FAQCmd.addFaq(key1, val1);
    FAQCmd.addFaq(key2, val2);
    assertTrue(FAQCmd.getFaq(key1).equals(val1));
    assertTrue(FAQCmd.getFaq(key2).equals(val2));
  }
  
  @Test
  public void testEditFAQ() {
    
    FAQCmd.addFaq("test1", "old-message");
    assertTrue(FAQCmd.hasFaq("test1"));
    assertTrue(FAQCmd.getFaq("test1").equals("old-message"));
    FAQCmd.addFaq("test1", "new-message");
    assertTrue(FAQCmd.getFaq("test1").equals("new-message"));
  }
  
  @Test
  public void testRemoveFAQ() {
    
    FAQCmd.addFaq("test1", "should be gone");
    assertTrue(FAQCmd.hasFaq("test1"));
    try {
      FAQCmd.removeFaq("test1");
    } catch (FAQNotFoundException e) {
      e.printStackTrace();
    }
    assertFalse(FAQCmd.hasFaq("test1"));
    
    boolean gotException = false;
    
    try {
      FAQCmd.removeFaq("this should not exist");
    } catch (@SuppressWarnings("unused") FAQNotFoundException e) {
      gotException = true;
    }
    
    assertTrue(gotException);
  }
}
