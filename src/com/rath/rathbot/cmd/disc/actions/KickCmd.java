
package com.rath.rathbot.cmd.disc.actions;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.msg.MessageHelper;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class KickCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "kick";
  }
  
  @Override
  public String getCommandDescription() {
    return "(Mod only) Kicks a user defined by their UID.";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! kick <uid> <reason..>";
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
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokDepth) {
    
    // Ensures at least minimum valid arguments used.
    if (tokens.length < 4) {
      RathBot.sendMessage(msg.getChannel(), "Syntax Error! Usage: rb! kick <uid> <reason>");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    long kickUserID = 0;
    
    // Retrieve UID from arguments.
    try {
      kickUserID = Long.parseLong(tokens[tokDepth + 1]);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    
    final IDiscordClient client = RathBot.getClient();
    
    // Create IUser object from REPORTED_USER_ID to reference user in messages.
    IUser kickedUser = client.getUserByID(kickUserID);
    if (kickedUser == null) {
      RathBot.sendDirectMessage(msg.getAuthor(), "Error! User not found, please enter a valid UID.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    RathBot.kickUser(kickedUser, msg.getTimestamp().getEpochSecond(),
        MessageHelper.concatenateTokens(tokens, tokDepth + 2));
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}