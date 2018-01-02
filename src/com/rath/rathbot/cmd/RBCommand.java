
package com.rath.rathbot.cmd;

import java.util.Set;

import com.rath.rathbot.RathBot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class forms the skeleton of a command.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public abstract class RBCommand {
  
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
   * Whether this command requires the user to be a moderator or higher to use.
   * 
   * @return true if elevated permissions are required to use; false if not.
   */
  public abstract boolean requiresModStatus();
  
  /**
   * Gets the sub-commands this command has.
   * 
   * @return a Set of RBCommand classes.
   */
  public abstract Set<RBCommand> getSubcommands();
  
  /**
   * Executes the command. Any external data needed must first be added to the child command's class via other
   * non-inherited methods.
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
      System.out.println("cmd is null");
      return false;
    } else {
      
      // Valid subcommand found, so return true
      cmd.executeCommand(rb, author, channel, tokens, tokenDepth + 1);
      return true;
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

// @formatter:off
/*

FAQCmd: 1
  FAQListCmd: 0
    null
  FAQEditCmd: 2+
    null
  FAQRemoveCmd, 1
    null
    
HelpCmd: {0,1}










:: Planned Commands (prefix: "rb!") ::

> faq <name>: posts the contents of the $name FAQ
  - list: lists all stored FAQs
  - edit* <name> <content..>: Edits the contents of FAQ $name to $content, or creates it if it does not exist
  - remove* <name>: Removes a FAQ from memory.
> report <username> <reason..>: Only accepted through a PM. Reports $username for $reason. The report will get posted in the admin chat.
> kick* <username> <reason..>: Kicks $username
> ban* <username> <time+> <reason..>: Bans $username.
> help [command]: Posts the commands list if $command is not given; otherwise, posts the help text for $command.
> poll
  - create <title> <time> <single/multichoice> {options[;,]}: Creates a poll with the specified title for the specified length of time ("3m20s" would be 3 minutes and 20 seconds). Poll options are semicolon-separated and there must be at least two choices. Choices will be numbered for easy voting.
  - vote <choices,>: Casts a vote for a choice or multiple comma-separated choices if enabled
  - close: closes the currently open poll
> memtime <user>: Post how long $user has been a member of this server
> sachis <user>: Posts how many Sachis $user has (server's currency)
  - add* <user> <amount>:Adds $amount Sachis to $user's account.
  - give <user> <amount>: Transfers $amount Sachis to
  - allowance* <user> <amount>: Changes the daily Sachi allowance for $member to $amount
> rules: Post a link to the server rules
> react <name>: Posts the reaction image named $name. Due to the possibility of registering potentially large images, Rath will add these manually. 

* Mod/Admin-only commands



:: Planned Triggers ::

> on new member join: PM rules link, log and remember join time
> spam: 
  - Threshold #1: 5 messages in 1 second.
  - Threshold #2: 20 messages in 10 seconds.
  - Threshold #3: 7 repeat messages in 20 minutes.
  - Actions: 1st - Warn; Kick, log, post in admin chat; Ban, log, post in admin chat





*/
// @formatter:on
