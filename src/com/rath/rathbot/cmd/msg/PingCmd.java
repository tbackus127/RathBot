
package com.rath.rathbot.cmd.msg;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class PingCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "ping";
  }
  
  @Override
  public String getCommandDescription() {
    return "Ping RathBot and receive a response.";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! ping";
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
  public boolean executeCommand(final IUser author, final IChannel channel, final String[] tokens, final int tokDepth) {
    
    RathBot.sendMessage(channel, "Pong!");
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
