
package com.rath.rathbot.cmd.disc.actions;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.util.MessageHelper;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Bans user by UID or @mention for a given reason.
 * 
 * @author Nathan Lehenbauer lehenbnw@gmail.com
 * @author Tim Backus tbackus127@gmail.com
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
    return "rb! ban <uid|@mention> <reason..>";
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
    
    final IChannel channel = msg.getChannel();
    
    // Ensures at least minimum valid arguments used.
    if (tokens.length < 4) {
      RathBot.sendMessage(channel, "Syntax Error! Usage: rb! ban <uid|@mention> <reason>");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // Tests if the first argument of the command is an @mention or uid, then processes the argument accordingly.
    long banUserID = 0;
    final String userToken = tokens[tokDepth + 1];
    if (userToken.matches("<@!?\\d+>")) {
      
      int hasNickName = 0;
      
      if (userToken.charAt(2) == '!') hasNickName = 1;
      
      // If argument is @mention, substring to get UID
      try {
        banUserID = Long.parseLong(
            userToken.substring((userToken.indexOf('@') + hasNickName + 1), userToken.indexOf('>')));
      } catch (NumberFormatException nfe) {
        nfe.printStackTrace();
      }
    } else {
      
      // Else argument is UID, parse UID
      try {
        banUserID = Long.parseLong(tokens[tokDepth + 1]);
      } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
        RathBot.sendMessage(channel, "Invalid UID. UIDs must only contain numbers.");
      }
    }
    
    final IDiscordClient client = RathBot.getClient();
    
    // Create IUser object from banUserID to ban them, also verify valid UID.
    final IUser bannedUser = client.getUserByID(banUserID);
    if (bannedUser == null) {
      RathBot.sendMessage(channel, "Error! User not found, please enter a valid UID.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // If the issuer's permissions level is lower than or equal to the target's disallow the mute
    final IUser author = msg.getAuthor();
    if (PermissionsTable.getLevel(author.getLongID()) <= PermissionsTable.getLevel(banUserID)) {
      RathBot.sendMessage(channel, "Cannot ban a member with an equal or higher permission level.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    RathBot.banUser(author, bannedUser, msg.getTimestamp().getEpochSecond(),
        MessageHelper.concatenateTokens(tokens, tokDepth + 2));
    
    return RBCommand.STOP_CMD_SEARCH;
  }
}
