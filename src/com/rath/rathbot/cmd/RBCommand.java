
package com.rath.rathbot.cmd;

import java.util.Set;

import com.rath.rathbot.RathBot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class forms the skeleton of a command.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public abstract class RBCommand {
  
  /** The command name as searched for in chat. */
  protected final String commandName;
  
  /** The description of the command when help is called. */
  protected final String commandDescription;
  
  /** If this command requires the user to be a moderator or higher to use. */
  protected final boolean needsModStatus;
  
  /**
   * Superclass constructor. Can only be called from child classes.
   * 
   * @param name the command name.
   * @param mod true if this command requires elevated priviliges to use.
   * @param descr the command description.
   */
  protected RBCommand(final String name, final boolean mod, final String descr) {
    this.commandName = name;
    this.needsModStatus = mod;
    this.commandDescription = descr;
  }
  
  /**
   * Gets the name of this command.
   * 
   * @return the String the bot will respond to.
   */
  public final String getCommandName() {
    return this.commandName;
  }
  
  /**
   * Gets this command's description.
   * 
   * @return the full text that will be displayed in this command's help entry as a String.
   */
  public final String getCommandDescription() {
    return this.commandDescription;
  }
  
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
  public abstract void executeCommand(final RathBot rb, final IUser author, final IChannel channel,
      final String[] tokens);
  
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