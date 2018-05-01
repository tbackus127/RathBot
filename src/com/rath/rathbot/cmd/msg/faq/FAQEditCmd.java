
package com.rath.rathbot.cmd.msg.faq;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.msg.MessageHelper;

import sx.blah.discord.handle.obj.IMessage;

/**
 * Subcommand of 'faq' that allows editing of FAQs.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class FAQEditCmd extends RBCommand {
  
  /** The subcommand's name that the bot checks for. */
  private static final String CMD_NAME = "edit";
  
  /** The description of this subcommand. */
  private static final String CMD_DESCR = "Edits the contents of FAQ with name=<NAME> to have contents=<CONTENTS>"
      + " if the FAQ entry exists. Otherwise, created a new FAQ with name=<NAME> and with contents=<CONTENTS>.";
  
  /** The usage message when the command has a syntactic or semantic error. */
  private static final String CMD_USAGE = "rb! faq edit <faqName> <contents>";
  
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
    return CMD_USAGE;
  }
  
  @Override
  public int permissionLevelRequired() {
    return RBCommand.PERM_MODERATOR;
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return false;
  }
  
  @Override
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokenDepth) {
    
    System.out.println("Executing faq.edit");
    
    final String message = MessageHelper.concatenateTokens(tokens, 4);
    
    FAQCmd.addFaq(tokens[3], message);
    RathBot.sendMessage(msg.getChannel(), "FAQ " + tokens[3] + " updated.");
    
    return RBCommand.STOP_CMD_SEARCH;
    
  }
  
}
