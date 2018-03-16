
package com.rath.rathbot.cmd.faq;

import java.util.Set;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.exceptions.FAQNotFoundException;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

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
  public Set<RBCommand> getSubcommands() {
    
    return null;
  }
  
  @Override
  public boolean executeCommand(final RathBot rb, final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    if (!super.executeCommand(rb, author, channel, tokens, tokenDepth)) {
      System.out.println("Executing faq.remove");
      
      // Prevent ArrayIndexOutOfBoundsExceptions
      if (tokens.length <= 3) return true;
      
      // Get the FAQ name and check if it exists
      final String faqName = tokens[3];
      if (FAQCmd.hasFaq(faqName)) {
        try {
          
          // Remove and send confirmation
          FAQCmd.removeFaq(faqName);
          rb.sendMessage(channel, "FAQ \"" + faqName + "\" removed.");
        } catch (FAQNotFoundException e) {
          
          // Inform that it doesn't exist
          rb.sendMessage(channel, "This FAQ doesn't exist.");
        }
      }
    }
    return true;
    
  }
  
  @Override
  public int permissionLevelRequired() {
    return RBCommand.PERM_MODERATOR;
  }
  
  @Override
  public void setupCommand() {
    return;
  }
  
}
