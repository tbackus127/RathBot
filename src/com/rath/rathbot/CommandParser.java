
package com.rath.rathbot;

import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.data.PermissionsTable;

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
      System.out.println("Message length < 2. Stopping dispatch.");
      return;
    }
    
    // Iterate through each command registered by the bot
    for (RBCommand cmd : bot.getCommandSet()) {
      
      System.out.println("Checking root command \"" + tokens[1].trim() + "\" against command " + cmd.getCommandName());
      
      // Check the token after the prefix against commands until we find a match
      if (tokens[1].trim().equalsIgnoreCase(cmd.getCommandName())) {
        
        final long userID = author.getLongID();
        
        // If the user doesn't have an entry on the permissions table, initialize them and save
        if (!PermissionsTable.hasUser(userID)) {
          PermissionsTable.initUser(userID);
        }
        
        // Check permissions for this command.
        if (PermissionsTable.getLevel(userID) >= cmd.permissionLevelRequired()) {
          
          // Execute the command that matches
          cmd.executeCommand(bot, author, channel, tokens, 1);
        } else {
          System.out.println(
              "User " + author.getName() + " tried to execute " + cmd.getCommandName() + " with permission level "
                  + PermissionsTable.getLevel(userID) + " (" + cmd.permissionLevelRequired() + " required).");
          bot.sendMessage(channel, "You do not have the required permissions for that command.");
        }
        
        break;
      }
    }
  }
}
