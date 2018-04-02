
package com.rath.rathbot;

import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
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
   * @param channel the IChannel the bot received the message on.
   * @param author the author of the message as an IAuthor object.
   * @param message the message itself.
   */
  public static final void parseCommand(final IMessage message) {
    
    final String msgString = message.getContent();
    final IUser author = message.getAuthor();
    final IChannel channel = message.getChannel();
    
    System.out.println("Parsing command: \"" + msgString + "\".");
    
    // Split into tokens separated by spaces, ignoring spaces between quotes
    final String[] tokens = msgString.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
    
    // Ensure the message isn't just the prefix
    if (tokens.length <= 1) {
      return;
    }
    
    // Extract the command name and command reference
    final String cmdName = tokens[1].trim().toLowerCase();
    final RBCommand cmd = RathBot.getCommandMap().get(cmdName);
    
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
      
      // If a PM-only command was issued in a public channel
      if (cmd.requiresDirectMessage() && RathBot.getChannelMap().values().contains(channel)) {
        
        // Delete the message and post a notification.
        message.delete();
        RathBot.sendMessage(channel, "This command can only be issued in a direct message to RathBot.");
        return;
      }
      
      // Execute the command that matches
      cmd.executeCommand(author, channel, tokens, 1);
      
    } else {
      System.out
          .println("User " + author.getName() + " tried to execute " + cmd.getCommandName() + " with permission level "
              + PermissionsTable.getLevel(userID) + " (" + cmd.permissionLevelRequired() + " required).");
      RathBot.sendMessage(channel, "You do not have the required permissions for that command.");
    }
    
  }
}
