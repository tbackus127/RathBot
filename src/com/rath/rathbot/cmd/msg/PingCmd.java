
package com.rath.rathbot.cmd.msg;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IMessage;

/**
 * 
 * This command pings the bot, prompting the bot to respond and verify the bot is processing commands.
 * 
 * @author Nathan Lehenbauer lehenbnw@gmail.com
 *
 */

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
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokDepth) {
    
    RathBot.sendMessage(msg.getChannel(), "Pong!");
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
