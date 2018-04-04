// ** IMPORTANT! ** Do NOT auto-format this file without first making sure your IDE supports the @formatter:off
//   and @formatter:on annotations!

package com.rath.rathbot.cmd;

import java.util.Set;

import com.rath.rathbot.RathBot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class forms the skeleton of a command. In addition to the abstract methods here, you *must* override the
 * executeCommand() method. See the template at the bottom of this file for details.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public abstract class RBCommand {
  
  /** Permission level: Sudo */
  public static final int PERM_SUDO = 7;
  
  /** Permission level: Admin */
  public static final int PERM_ADMIN = 6;
  
  /** Permission level: Moderator */
  public static final int PERM_MODERATOR = 5;
  
  /** Permission level: Sub-mod */
  public static final int PERM_SUBMOD = 4;
  
  /** Permission level: Trusted */
  public static final int PERM_TRUSTED = 3;
  
  /** Permission level: Standard */
  public static final int PERM_STANDARD = 2;
  
  /** Permission level: Minimal */
  public static final int PERM_MINIMAL = 1;
  
  /** Permission level: Forbid */
  public static final int PERM_FORBID = 0;
  
  /** Alias to a valid command being found (true). */
  public static final boolean STOP_CMD_SEARCH = true;
  
  /** Alias to a valid command not being found (false). */
  public static final boolean CONTINUE_CMD_SEARCH = false;
  
  /**
   * Gets the name of this command.
   * 
   * @return the String the bot will respond to.
   */
  public abstract String getCommandName();
  
  /**
   * Gets this command's description.
   * 
   * @return the full text that will be displayed in this command's help entry as a String.
   */
  public abstract String getCommandDescription();
  
  /**
   * Gets this command's usage syntax in case an error was made.
   * 
   * @return the command's usage text as a String.
   */
  public abstract String getCommandUsage();
  
  /**
   * The permission level of the issuer that must be met or exceeded for the command to execute.
   * 
   * @return the permission level as an int. Permission constants are declared in the RBCommand class.
   */
  public abstract int permissionLevelRequired();
  
  /**
   * Whether or not the command can only be issued through a direct message to RathBot.
   * 
   * @return true if it needs a DM; false if not.
   */
  public abstract boolean requiresDirectMessage();
  
  /**
   * Performs various initialization operations for commands, such as loading data from a file, building tables, or
   * anything else necessary to execute before the command can be issued. If no setup is required, overriding this
   * method is unnecessary.
   */
  public void setupCommand() {
    return;
  };
  
  /**
   * Gets the sub-commands this command has. Override this method if your command has sub-commands.
   * 
   * @return a Set of RBCommand classes. Use a HashSet for these, not a TreeSet.
   */
  public Set<RBCommand> getSubcommands() {
    return null;
  };
  
  /**
   * Executes the command. Any external data needed must first be added to the child command's class via other
   * non-inherited methods. See /ref/templates.txt for instructions to correctly override this method.
   * 
   * @param author the IUser that executed the command.
   * @param channel the channel the command was received from.
   * @param tokens an array of Strings that were split at commas.
   * @param tokenDepth the index of the token we are working with.
   * @return true if the recursive command search can be stopped; false if not.
   */
  public boolean executeCommand(final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    System.out.println("In executeCommand() for " + this.getCommandName() + " with td=" + tokenDepth);
    
    // Check this command's subcommands for a match, and return the matched command
    final RBCommand cmd = checkSubcommands(getSubcommands(), tokens, tokenDepth);
    
    // If a subcommand is not found
    if (cmd == null) {
      System.out.println("No subcommands found for " + tokens[tokenDepth]);
      return RBCommand.CONTINUE_CMD_SEARCH;
    } else {
      
      // Check permissions for this command.
      final long userID = author.getLongID();
      if (PermissionsTable.getLevel(userID) >= cmd.permissionLevelRequired()) {
        
        // Valid subcommand found, so return true
        cmd.executeCommand(author, channel, tokens, tokenDepth + 1);
        return RBCommand.STOP_CMD_SEARCH;
        
      } else {
        
        // Insufficient permissions. Log and notify.
        System.out.println(
            "User " + author.getName() + " tried to execute " + cmd.getCommandName() + " with permission level "
                + PermissionsTable.getLevel(userID) + " (" + cmd.permissionLevelRequired() + " required).");
        RathBot.sendMessage(channel, "You do not have the required permissions for that command.");
        
        return RBCommand.STOP_CMD_SEARCH;
      }
      
    }
  }
  
  /**
   * Checks the command's subcommands against the given tokens for a command name match.
   * 
   * @param subcommands the command's subcommands.
   * @param tokens the tokens in the author's message.
   * @param tokenDepth which token we're checking.
   * @return the RBCommand that matches the token; null if nothing matches.
   */
  protected static final RBCommand checkSubcommands(final Set<RBCommand> subcommands, final String[] tokens,
      final int tokenDepth) {
    
    System.out.println("Checking subcommands of " + tokens[tokenDepth] + ":");
    
    // If there are no subcommands, return null
    if (subcommands == null) {
      System.out.println("  " + tokens[tokenDepth] + " has no subcommands.");
      return null;
    }
    
    if (tokenDepth + 1 >= tokens.length) {
      System.out.println("Tokens exhausted.");
      return null;
    }
    
    // Go through each subcommand and check the correct token for a match
    for (final RBCommand cmd : subcommands) {
      
      System.out.println(
          "  Checking \"" + tokens[tokenDepth + 1] + "\" against subcommand \"" + cmd.getCommandName() + "\".");
      if (cmd.getCommandName().equalsIgnoreCase(tokens[tokenDepth + 1])) {
        
        System.out.println("  Matched with " + cmd.getCommandName());
        return cmd;
      }
    }
    
    // No valid subcommand was found, so return null
    System.out.println("  No subcommands found for token.");
    return null;
    
  }
  
}
