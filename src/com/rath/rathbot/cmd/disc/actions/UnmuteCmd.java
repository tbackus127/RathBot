
package com.rath.rathbot.cmd.disc.actions;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Unmutes a user. The user will no longer have their messages immediately deleted.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class UnmuteCmd extends RBCommand {
  
  /** The command name for the bot. */
  private static final String CMD_NAME = "unmute";
  
  /** The command description for the bot's help entry. */
  private static final String CMD_DESC = "Unmutes a member.";
  
  /** The command's syntax. */
  private static final String CMD_USAGE = "rb! unmute <UserID#>";
  
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
    if (tokens.length != 3) {
      RathBot.sendMessage(channel, "Usage: \"" + getCommandUsage() + "\".");
      return true;
    }
    
    // Parse the user's long ID
    long member = -1;
    try {
      member = Long.parseLong(tokens[2]);
    } catch (NumberFormatException nfe) {
      RathBot.sendMessage(channel, "Usage: \"" + getCommandUsage() + "\".");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    final IUser author = msg.getAuthor();
    final IUser unmutedUser = RathBot.getClient().getUserByID(member);
    RathBot.unmuteUser(author, unmutedUser);
    RathBot.sendMessage(channel, unmutedUser + " has been unmuted.");
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
