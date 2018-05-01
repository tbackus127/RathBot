
package com.rath.rathbot.cmd.disc.actions;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.msg.MessageHelper;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Bans user by UID or mention for a given reason.
 * 
 * @author nlehenba
 *
 */
public class BanCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "ban";
  }
  
  @Override
  public String getCommandDescription() {
    return "(Mod only) Bans a user defined by their UID or @mention for a given reason.";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! ban <uid> <reason..>";
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
    
    // TODO: create a check for user mention or UID, use either to create user object.
    
    long banUserID = 0;
    
    // Retrieve UID from arguments.
    try {
      banUserID = Long.parseLong(tokens[tokDepth + 1]);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    
    final IDiscordClient client = RathBot.getClient();
    
    // Create IUser object from banUserID to ban them.
    IUser bannedUser = client.getUserByID(banUserID);
    if (bannedUser == null) {
      RathBot.sendDirectMessage(msg.getAuthor(), "Error! User not found, please enter a valid UID.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    RathBot.banUser(bannedUser, msg.getTimestamp().getEpochSecond(),
        MessageHelper.concatenateTokens(tokens, tokDepth + 2));
    
    return RBCommand.STOP_CMD_SEARCH;
  }
}
