
package com.rath.rathbot;

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

    // Split into tokens separated by spaces, ignoring spaces between quotes
    final String[] tokens = message.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");

    // Ensure the message isn't just the prefix
    if (tokens.length <= 1) {
      return;
    }

    // Iterate through each command registered by the bot
    for (RBCommand cmd : bot.getCommandSet()) {

      // Check the token after the prefix against commands until we find a match
      if (tokens[1].trim().equalsIgnoreCase(cmd.getCommandName())) {

        // Execute the command that matches
        cmd.executeCommand(bot, author, channel, tokens);
        break;
      }
    }
  }
}
