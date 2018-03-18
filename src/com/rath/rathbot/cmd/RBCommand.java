// ** IMPORTANT! ** Do NOT auto-format this file without first making sure your IDE supports the @formatter:off
//   and @formatter:on annotations!

package com.rath.rathbot.cmd;

import java.util.Set;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.data.PermissionsTable;

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
   * Executes the command. Any external data needed must first be added to the child command's class via other
   * non-inherited methods. See the bottom of RBCommand.java for templates on overriding this method.
   * 
   * @param rb Reference to the bot.
   * @param channel the channel the command was received from.
   * @param message the message that executed the command.
   */
  public boolean executeCommand(final RathBot rb, final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    System.out.println("In executeCommand() for " + this.getCommandName() + "with td=" + tokenDepth);
    
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
        cmd.executeCommand(rb, author, channel, tokens, tokenDepth + 1);
        return RBCommand.STOP_CMD_SEARCH;
        
      } else {
        
        // Insufficient permissions. Log and notify.
        System.out.println(
            "User " + author.getName() + " tried to execute " + cmd.getCommandName() + " with permission level "
                + PermissionsTable.getLevel(userID) + " (" + cmd.permissionLevelRequired() + " required).");
        rb.sendMessage(channel, "You do not have the required permissions for that command.");
        
        return RBCommand.STOP_CMD_SEARCH;
      }
      
    }
  }
  
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

// @formatter:off
/*

:: Command Templates ::

> If the command has subcommands, you will need to add this method:
@Override
public boolean executeCommand(RathBot rb, IUser author, IChannel ch, String[] tokens, int tokDepth) {
  if (!super.executeCommand(rb, author, ch, tokens, tokDepth)) {
    [COMMAND BODY -- Executes if there are no sub-commands]
  }  
  return RBCommand.STOP_CMD_SEARCH;
}

> If the command does not have subcommands, you use this one:
@Override
public boolean executeCommand(final RathBot rb, final IUser author, final IChannel channel, final String[] tokens, final int tokenDepth) {
  [COMMAND BODY -- Executes unconditionally]
  return RBCommand.STOP_CMD_SEARCH;
}



:: Permission Levels ::

7 - Sudo: Rath & Kami 
6 - Admin: Administrators
5 - Moderator: Moderators
4 - Sub-mod: Moderators that are in a probationary period.
3 - Trusted: Users who have shown they can be mature and won't abuse the more advanced features of the bot.
2 - Standard: Normal users
1 - Minimal: Users who require a restricted command set because of command abuse.
0 - Forbid: Users who cannot use the bot.



:: Planned Commands (prefix: "rb!") ::

Required permission levels are in parentheses preceded by 'P': "(P4)" means the command requires level 4+.

> Fun
[ ] poll
  [ ] (P3) create <title> <time> <exc(lusive)/multi(-choice)> {options[;,]}: Creates a poll with the specified title
      for the specified length of time ("3m20s" would be 3 minutes and 20 seconds). Poll options are
      semicolon-separated and there must be at least two choices. Choices will be numbered for easy voting.
  [ ] (P1) vote <choices,>: Casts a vote for a choice or multiple comma-separated choices if enabled
  [ ] (P2) close: closes the currently open poll
[ ] (P2) sachis <user>: Posts how many Sachis $user has (server's currency, because Sachi is Best Grisaia Girl)
  [ ] (P4) add* <user> <amount>: Adds $amount Sachis to $user's account.
  [ ] (P2) give <user> <amount>: Transfers $amount Sachis to
  [ ] (P5) allowance* <user> <amount>: Changes the daily Sachi allowance for $member to $amount
[ ] (P2) react <name>: Posts the reaction image named $name. Due to the possibility of registering potentially large
    images, Rath will add these manually. They can be hot-swapped while the bot is running.

> Info
[X] (P1) help [command]: Posts the commands list if $command is not given; otherwise, posts the help text for $command.
[X] (P2) faq <name>: posts the contents of the $name FAQ
  [X] (P2) list: lists all stored FAQs
  [X] (P3) edit* <name> <content..>: Edits the contents of FAQ $name to $content, or creates it if it does not exist
  [X] (P4) remove* <name>: Removes a FAQ from memory.
[ ] (P1) uid**: Gets the internal ID for a user. This is needed for anonymous reports.
[ ] memstats
  [ ] (P2) memtime <user>: Post how long $user has been a member of this server.

> Administrative
[ ] (P1) report** <UID> <reason..>: Only accepted through a PM. Reports the member with UID = $uid for $reason. The
    report will get posted in #report and will be logged.
[ ] (P4) kick* <username> <reason..>: Kicks $username
[ ] (P5) ban* <username> <time+> <reason..>: Bans $username.
[ ] (P4) mute* <username> <time+> <reason..>: $username's new messages will be immediately deleted for $time. Posting
    too many messages during this time will result in a kick. A $reason may be given.
[ ] (P1) rule <X.Y.Z>: Posts rule X.Y.Z. Example: "rb! rule 14.1" will post "14.1: The Discord EULA overrides all rules
    set forth by this server."

> Bot-related
[ ] (P6) perm* <uid> <level>: Sets the user with UID=$uid to $level.
[ ] (P7) shutdown: Shuts the bot down as a last resort in case something goes haywire.
[ ] (P7) restart: Shuts the bot down and starts it back up.
[ ] (P7) rebuild: Deletes all saved data structures and rebuilds them.
[ ] (P3) running: Posts how long the bot has been running.


* Mod/Admin-only commands
** Must be done through a private message to RathBot

*/
// @formatter:on
