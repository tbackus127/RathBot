
package com.rath.rathbot.cmd.disc.actions;

import java.time.Instant;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.action.ActionKick;
import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.log.ActionLogger;
import com.rath.rathbot.util.MessageHelper;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Kicks user by UID or @mention for a given reason.
 * 
 * @author Nathan Lehenbauer lehenbnw@gmail.com
 * @author Tim Backus tbackus127@gmail.com
 *
 */

public class KickCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "kick";
  }
  
  @Override
  public String getCommandDescription() {
    return "(Mod only) Kicks a user defined by their UID or @mention for a given reason.";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! kick <uid|@mention> <reason..>";
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
      RathBot.sendMessage(channel, "Syntax Error! Usage: rb! kick <uid|@mention> <reason>");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // Create IUser object from token to issue disciplinary action on them.
    final IUser infringingUser = MessageHelper.getUserFromToken(tokens[tokDepth + 1], channel);
    if (infringingUser == null) {
      RathBot.sendMessage(channel,
          "Error: Invalid UID or User not Found! Please verify User exists and UID is correct. Remember: UIDs should only contain numbers.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // If the issuer's permissions level is lower than or equal to the target's disallow the command.
    final IUser author = msg.getAuthor();
    if (PermissionsTable.getLevel(author.getLongID()) <= PermissionsTable.getLevel(infringingUser.getLongID())) {
      RathBot.sendMessage(channel, "Cannot kick a member with an equal or higher permission level.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    RathBot.kickUser(author, infringingUser, msg.getTimestamp().getEpochSecond(),
        MessageHelper.concatenateTokens(tokens, tokDepth + 2));
    
    // Log kick.
    ActionLogger.logAction(new ActionKick(Instant.now(), author, infringingUser));
    
    RathBot.sendMessage(channel, infringingUser.getName() + " has been kicked.");
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}