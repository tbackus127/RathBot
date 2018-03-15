
package com.rath.rathbot.cmd.faq;

import java.util.Set;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

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
      System.out.println("Executing faq.list");
    }
    
    return true;
    
  }
  
  @Override
  public int permissionLevelRequired() {
    return RBCommand.PERM_STANDARD;
  }

  @Override
  public void setupCommand() {
    return;
  }
  
}
