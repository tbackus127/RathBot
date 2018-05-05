
package com.rath.rathbot.cmd.disc.actions;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.msg.MessageHelper;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Mutes a user. Muting will not completely prevent a player from posting messages (since that feature doesn't exist in
 * Discord; devs pls), but will instead immediately delete the muted user's messages until they are unmuted.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class MuteCmd extends RBCommand {
  
  /** The command name for the bot. */
  private static final String CMD_NAME = "mute";
  
  /** The command description for the bot's help entry. */
  private static final String CMD_DESC = "A member will have their messages immediately deleted for the"
      + " specified period of time.";
  
  /** The command's syntax. */
  // TODO: Change token 2 to work with mentions
  private static final String CMD_USAGE = "rb! mute <uid|@mention> <duration> <reason..>";
  
  @Override
  public String getCommandName() {
    return CMD_NAME;
  }
  
  @Override
  public String getCommandDescription() {
    return CMD_DESC;
  }
  
  @Override
  public String getCommandUsage() {
    return CMD_USAGE;
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
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokenDepth) {
    
    // Ensure proper token length
    final IChannel channel = msg.getChannel();
    if (tokens.length < 4) {
      RathBot.sendMessage(channel, "Usage: \"" + getCommandUsage() + "\".");
      return true;
    }
    
    // Parse the user's long ID
    long mutedUserUID = -1;
    try {
      mutedUserUID = Long.parseLong(tokens[2]);
    } catch (NumberFormatException nfe) {
      RathBot.sendMessage(channel, "Usage: \"" + getCommandUsage() + "\".");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // If the issuer's permissions level is lower than or equal to the target's disallow the mute
    final IUser author = msg.getAuthor();
    if (PermissionsTable.getLevel(msg.getAuthor().getLongID()) <= PermissionsTable.getLevel(mutedUserUID)) {
      RathBot.sendMessage(channel, "Cannot mute a member with a higher permission level.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // Parse the mute duration
    // TODO: Make method for Y/M/W/d/h/m/s conversion to seconds
    final int muteDuration = 0;
    
    // TODO: Parse mute duration and reason in tokens
    final String muteReason = MessageHelper.concatenateTokens(tokens, 4);
    final IUser mutedUser = RathBot.getClient().getUserByID(mutedUserUID);
    RathBot.muteUser(author, mutedUser, msg.getTimestamp().getEpochSecond(), muteDuration, muteReason);
    RathBot.sendMessage(channel, mutedUser.getName() + " has been muted.");
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
