
package com.rath.rathbot.cmd.disc.actions;

import java.time.Instant;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.action.ActionWarn;
import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.log.ActionLogger;
import com.rath.rathbot.util.MessageHelper;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Warns user by UID or @mention for a given reason.
 * 
 * @author Nathan Lehenbauer lehenbnw@gmail.com
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class WarnCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "warn";
  }
  
  @Override
  public String getCommandDescription() {
    return "(Mod only) Warns a user defined by their UID or @mention.";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! warn <uid|@mention>";
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
      RathBot.sendMessage(channel, "Syntax Error! Usage: " + this.getCommandUsage());
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // Create IUser object from token to issue disciplinary action on them.
    final IUser infringingUser = MessageHelper.getUserFromToken(tokens[tokDepth + 1], channel);
    if (infringingUser == null) {
      RathBot.sendMessage(channel,
          "The given username or user ID was not found. Ensure that you've entered the member's username or user ID correctly.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    final IUser author = msg.getAuthor();
    // If the issuer's permissions level is lower than or equal to the target's disallow the command.
    if (PermissionsTable.getLevel(author.getLongID()) <= PermissionsTable.getLevel(infringingUser.getLongID())) {
      RathBot.sendMessage(channel, "Cannot warn a member with an equal or higher permission level.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    RathBot.warnUser(author, infringingUser, msg.getTimestamp().getEpochSecond(),
        MessageHelper.concatenateTokens(tokens, tokDepth + 2));
    
    // Log warn.
    ActionLogger.logAction(new ActionWarn(Instant.now(), author, infringingUser));
    
    RathBot.sendMessage(channel, infringingUser.getName() + " has been warned.");
    
    return RBCommand.STOP_CMD_SEARCH;
  }
}
