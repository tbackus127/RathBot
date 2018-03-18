
package com.rath.rathbot.cmd.admin;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class MuteCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "mute";
  }
  
  @Override
  public String getCommandDescription() {
    return "A member will have their messages immediately deleted for the specified period of time.";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! mute <ID> <Duration>";
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
  public boolean executeCommand(final RathBot rb, final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    // UserInfractions.setMuted(member, true);
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
