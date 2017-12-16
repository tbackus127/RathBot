
package com.rath.rathbot.cmd;

import java.util.Set;

import com.rath.rathbot.RathBot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class HelpCmd extends RBCommand {
  
  public HelpCmd() {
    super("help", false, "Displays help messages for other commands.");
  }
  
  public final void addCommandEntry(final RBCommand cmd) {
    
  }
  
  @Override
  public Set<RBCommand> getSubcommands() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public void executeCommand(RathBot rb, IUser author, IChannel channel, String[] tokens) {
    // TODO Auto-generated method stub
    
  }
  
}
