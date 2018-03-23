
package com.rath.rathbot.cmd.userpunishments;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.disc.Infractions;

import sx.blah.discord.handle.obj.IChannel;
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
  private static final String CMD_USAGE = "rb! mute <UserID#> <Duration>";
  
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
  public boolean executeCommand(final RathBot rb, final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    // Ensure proper token length
    if (tokens.length != 4) {
      rb.sendMessage(channel, "Usage: \"" + getCommandUsage() + "\".");
      return true;
    }
    
    // Parse the user's long ID
    long member = -1;
    try {
      member = Long.parseLong(tokens[2]);
    } catch (NumberFormatException nfe) {
      rb.sendMessage(channel, "Usage: \"" + getCommandUsage() + "\".");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // If the issuer's permissions level is lower than or equal to the target's disallow the mute
    if (PermissionsTable.getLevel(author.getLongID()) <= PermissionsTable.getLevel(member)) {
      rb.sendMessage(channel, "Cannot mute a member with a higher permission level.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    Infractions.setMuted(member, true);
    rb.sendMessage(channel, rb.getClient().getUserByID(member).getName() + " has been muted.");
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
