
package com.rath.rathbot.cmd.msg.faq;

import com.rath.rathbot.DBG;
import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IMessage;

/**
 * This subcommand outputs a list of all saved FAQs.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class FAQListCmd extends RBCommand {
  
  private static final String CMD_NAME = "list";
  
  private static final String CMD_DESCR = "Displays a list of all available FAQs.";
  
  @Override
  public String getCommandName() {
    
    return CMD_NAME;
  }
  
  @Override
  public String getCommandDescription() {
    
    return CMD_DESCR;
  }
  
  @Override
  public String getCommandUsage() {
    // TODO: getCommandUsage for FAQListCmd
    return null;
  }
  
  @Override
  public int permissionLevelRequired() {
    return RBCommand.PERM_STANDARD;
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return false;
  }
  
  @Override
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokenDepth) {
    
    DBG.pl("Executing faq.list.");
    RathBot.sendMessage(msg.getChannel(), FAQCmd.getFaqList());
    
    return RBCommand.STOP_CMD_SEARCH;
    
  }
  
}
