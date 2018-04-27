
package com.rath.rathbot.cmd.msg;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class SayHelloCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "sayhello";
  }
  
  @Override
  public String getCommandDescription() {
    return "Make RathBot say \"hello\".";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! sayHello";
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
    
    RathBot.sendMessage(channel, "Hello!");
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
