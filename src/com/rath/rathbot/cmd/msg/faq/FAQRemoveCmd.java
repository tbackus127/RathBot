
package com.rath.rathbot.cmd.msg.faq;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.exceptions.FAQNotFoundException;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This subcommand of the 'faq' command allows mods to remove mappings from the FAQ table.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class FAQRemoveCmd extends RBCommand {
  
  /** The name of this command expected by the bot. */
  private static final String CMD_NAME = "remove";
  
  /** This command's description. */
  private static final String CMD_DESCR = "Removes a mapping for FAQ name=<NAME>.";
  
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
    // TODO Auto-generated method stub
    return null;
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
    
    System.out.println("Executing faq.remove");
    
    // Prevent ArrayIndexOutOfBoundsExceptions
    if (tokens.length <= 3) return true;
    
    // Get the FAQ name and check if it exists
    final String faqName = tokens[3];
    final IChannel channel = msg.getChannel();
    if (FAQCmd.hasFaq(faqName)) {
      try {
        
        // Remove and send confirmation
        FAQCmd.removeFaq(faqName);
        RathBot.sendMessage(channel, "FAQ \"" + faqName + "\" removed.");
      } catch (@SuppressWarnings("unused") FAQNotFoundException e) {
        
        // Inform that it doesn't exist
        RathBot.sendMessage(channel, "This FAQ doesn't exist.");
      }
    }
    return RBCommand.STOP_CMD_SEARCH;
    
  }
  
}
