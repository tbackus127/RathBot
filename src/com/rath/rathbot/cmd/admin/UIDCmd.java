
package com.rath.rathbot.cmd.admin;

import java.util.ArrayList;
import java.util.List;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.msg.MessageHelper;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This command gets a user's unique long ID, needed for specifying users in direct-message-only commands.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class UIDCmd extends RBCommand {
  
  /** The command name. */
  private static final String CMD_NAME = "uid";
  
  /** The command's description. */
  private static final String CMD_DESC = "Gets a member's unique internal ID, used for reports.";
  
  /** The command syntax. */
  private static final String CMD_USAGE = "rb! uid <MEMBER NAME>";
  
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
    return RBCommand.PERM_MINIMAL;
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return true;
  }
  
  @Override
  public boolean executeCommand(final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    // If there aren't enough commands, notify and stop
    if (tokens.length <= 2) {
      // print usage
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // Concatenate tokens and perform the search
    final String userSearch = MessageHelper.concatenateTokens(tokens, 2);
    final List<IUser> userMatches = RathBot.getClient().getUsersByName(userSearch, true);
    
    // Convert IUser list into String list
    final List<String> matchList = new ArrayList<String>();
    for (final IUser u : userMatches) {
      matchList.add(u.getName() + "#" + u.getDiscriminator() + ": " + u.getLongID());
    }
    
    // Build the list and send the message
    final String matchesStr = MessageHelper.buildListString("Matches for " + userSearch + ":", matchList, true);
    RathBot.sendMessage(channel, matchesStr);
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
