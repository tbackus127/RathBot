
package com.rath.rathbot.cmd.disc.actions;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Unbans a user by their UID or mention.
 * 
 * @author nlehenba
 *
 */
public class UnbanCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "unban";
  }
  
  @Override
  public String getCommandDescription() {
    return "(Mod only) Unbans a user defined by their UID or @mention.";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! unban <uid|mention>";
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
    if (tokens.length < 3) {
      RathBot.sendMessage(msg.getChannel(), "Syntax Error! Usage: rb! kick <uid>");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // TODO: create a check for user mention or UID, use either to create user object.
    
    long unbanUserID = 0;
    
    // Retrieve UID from arguments.
    try {
      unbanUserID = Long.parseLong(tokens[tokDepth + 1]);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    
    final IDiscordClient client = RathBot.getClient();
    
    // Create IUser object from REPORTED_USER_ID to reference user in messages.
    IUser unbannedUser = client.getUserByID(unbanUserID);
    if (unbannedUser == null) {
      RathBot.sendDirectMessage(msg.getAuthor(), "Error! User not found, please enter a valid UID.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    RathBot.unbanUser(unbannedUser);
    
    return RBCommand.STOP_CMD_SEARCH;
  }
}
