
package com.rath.rathbot;

import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class handles parsing and dispatching of commands.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class CommandParser {
  
  /**
   * Parses and dispatches a command to the proper methods.
   * 
   * @param bot reference to the RathBot instance.
   * @param channel the IChannel the bot received the message on.
   * @param author the author of the message as an IAuthor object.
   * @param message the message itself.
   */
  public static final void parseCommand(final RathBot bot, final IChannel channel, final IUser author,
      final String message) {
    
    System.out.println("Parsing command: \"" + message + "\".");
    
    // Split into tokens separated by spaces, ignoring spaces between quotes
    final String[] tokens = message.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
    
    // Ensure the message isn't just the prefix
    if (tokens.length <= 1) {
      return;
    }
    
    // Extract the command name and command reference
    final String cmdName = tokens[1].trim().toLowerCase();
    final RBCommand cmd = bot.getCommandMap().get(cmdName);
    
    // If no command exists
    if (cmd == null) {
      // TODO: Post invalid command message
      return;
    }
    
    // If the user doesn't have an entry on the permissions table, initialize them and save
    final long userID = author.getLongID();
    if (!PermissionsTable.hasUser(userID)) {
      PermissionsTable.initUser(userID);
    }
    
    // Check permissions for this command.
    if (PermissionsTable.getLevel(userID) >= cmd.permissionLevelRequired()) {
      
      // Execute the command that matches
      cmd.executeCommand(bot, author, channel, tokens, 1);
    } else {
      System.out
          .println("User " + author.getName() + " tried to execute " + cmd.getCommandName() + " with permission level "
              + PermissionsTable.getLevel(userID) + " (" + cmd.permissionLevelRequired() + " required).");
      bot.sendMessage(channel, "You do not have the required permissions for that command.");
    }
    
  }
}
