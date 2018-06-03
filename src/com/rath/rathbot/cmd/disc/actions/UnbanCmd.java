
package com.rath.rathbot.cmd.disc.actions;

import java.time.Instant;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.action.ActionUnban;
import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.log.ActionLogger;
import com.rath.rathbot.util.MessageHelper;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Unbans a user by their UID or @mention.
 * 
 * @author Nathan Lehenbauer lehenbnw@gmail.com
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
    
    final IChannel channel = msg.getChannel();
    // Ensures at least minimum valid arguments used.
    if (tokens.length < 3) {
      RathBot.sendMessage(channel, "Syntax Error! Usage: rb! unban <uid|@mention>");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // Create IUser object from token to issue disciplinary action on them.
    final IUser infringingUser = MessageHelper.getUserFromToken(tokens[tokDepth + 1]);
    if (infringingUser == null) {
      RathBot.sendMessage(channel,
          "Error: Invalid UID or User not Found! Please verify User exists and UID is correct. Remember: UIDs should only contain numbers.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // If the issuer's permissions level is lower than or equal to the target's disallow the command.
    final IUser author = msg.getAuthor();
    if (PermissionsTable.getLevel(msg.getAuthor().getLongID()) <= PermissionsTable.getLevel(
        infringingUser.getLongID())) {
      RathBot.sendMessage(channel, "Cannot unban a member with an equal or higher permission level.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // Unban the user.
    RathBot.unbanUser(author, infringingUser);
    RathBot.sendMessage(channel, infringingUser.getName() + " has been unbanned.");
    
    // Log unban.
    ActionLogger.logAction(new ActionUnban(Instant.now(), author, infringingUser));
    
    return RBCommand.STOP_CMD_SEARCH;
  }
}
