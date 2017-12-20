
package com.rath.rathbot;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.rath.rathbot.cmd.faq.FAQCmd;

public class TestFAQCmd {

  @Test
  public void testFAQAdd() {

    FAQCmd.addFaq("testa", "test-a-msg");
    FAQCmd.addFaq("testb", "test-b-msg");
    FAQCmd.addFaq("testc", "test-c-msg");
    final String list = FAQCmd.getFaqList();
    final String expected = "FAQ List:\n  testa\n  testb\n  testc\n";

    assertTrue(list.equals(expected));
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

}
