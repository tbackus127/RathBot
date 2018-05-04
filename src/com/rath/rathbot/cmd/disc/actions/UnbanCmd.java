
package com.rath.rathbot.cmd.disc.actions;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Unbans a user by their UID or @mention.
 * 
 * @author nlehenba
 * @author Tim Backus tbackus127@gmail.com
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
    
    // Tests if the first argument of the command is an @mention or uid, then processes the argument accordingly.
    long unbanUserID = 0;
    String userToken = tokens[tokDepth + 1];
    if (userToken.matches("<@!?\\d+>")) {
      
      int hasNickName = 0;
      
      if (userToken.matches("<@!\\d+>")) hasNickName = 1;
      
      // If argument is @mention, substring to get UID
      try {
        unbanUserID = Long.parseLong(
            userToken.substring((userToken.indexOf('@') + hasNickName + 1), userToken.indexOf('>')));
      } catch (NumberFormatException nfe) {
        nfe.printStackTrace();
      }
    } else {
      
      // Else argument is UID, parse UID
      try {
        unbanUserID = Long.parseLong(tokens[tokDepth + 1]);
      } catch (NumberFormatException nfe) {
        nfe.printStackTrace();
      }
    }
    
    final IDiscordClient client = RathBot.getClient();
    
    // Create IUser object from unbanUserID to reference user in messages.
    IUser unbannedUser = client.getUserByID(unbanUserID);
    if (unbannedUser == null) {
      RathBot.sendDirectMessage(msg.getAuthor(), "Error! User not found, please enter a valid UID.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // If the issuer's permissions level is lower than or equal to the target's disallow the mute
    if (PermissionsTable.getLevel(msg.getAuthor().getLongID()) <= PermissionsTable.getLevel(unbanUserID)) {
      RathBot.sendMessage(msg.getChannel(), "Cannot unban a member with an equal or higher permission level.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    RathBot.unbanUser(unbannedUser);
    
    return RBCommand.STOP_CMD_SEARCH;
  }
}
