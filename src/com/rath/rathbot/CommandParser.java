
package com.rath.rathbot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class handles parsing and dispatching of commands.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class CommandParser {
  
  /** The list of commands displayed when the "help" command is issued. */
  private static final String COMMAND_LIST = "**Commands**\n  prefix: \"rb!\"\n```"
      + "help [command] - Shows how to use the given command.```";
  
  /**
   * Parses and dispatches a command to the proper methods.
   * 
   * @param bot reference to the RathBot instance.
   * @param channel the IChannel the bot received the message on.
   * @param author the author of the message as an IAuthor object.
   * @param message the message itself.
   */
  public static final void parseCommand(final RathBot bot, final IChannel channel,
      final IUser author, final String message) {
    
    // Split into tokens separated by spaces, ignoring spaces between quotes
    final String[] tokens = message.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
    
    if (tokens.length <= 1) {
      sendErrorMessage(bot, channel);
    }
    
    // Check command / args
    switch (tokens[1]) {
      
      // Command list / command help
      case "help":
        if (tokens.length == 2) {
          sendCommandList(bot, channel);
        } else if (tokens.length == 3) {
          
          // Help pages
          switch (tokens[2]) {
            
            // TODO: Write help pages
            
          }
        } else {
          sendErrorMessage(bot, channel);
        }
        
        // Copypasta (we'll call them FAQs)
      case "faq":
        
        // list, <storedID>
        if (tokens.length == 3) {
          
          if (tokens[2].equals("list")) {
            bot.postFaqList(channel);
          } else if (bot.hasFaq(tokens[2])) {
            bot.postFaq(tokens[2], channel);
          } else {
            bot.sendMessage(channel, "No FAQ available for that ID.");
          }
          
          // remove
        } else if (tokens.length == 4 && tokens[2].equals("remove")) {
          
          final String faqName = tokens[3];
          
          // Ensure the entry exists
          if (bot.hasFaq(faqName)) {
            bot.removeFaq(faqName);
            bot.sendMessage(channel, "FAQ removed.");
          } else {
            bot.sendMessage(channel, "No FAQ available for that ID.");
          }
          
        } else if (tokens.length == 5 && tokens[2].equals("add")) {
          
          final String faqName = tokens[3];
          bot.addFaq(faqName, tokens[4]);
          
          // Use a different message for new FAQs.
          if (bot.hasFaq(faqName)) {
            bot.sendMessage(channel, "Updated FAQ: \"*" + faqName + "\"*.");
          } else {
            bot.sendMessage(channel, "Added FAQ: \"*" + faqName + "\"*.");
          }
          
        } else {
          sendErrorMessage(bot, channel);
        }
        
      break;
    
      // Dice roll
      case "roll":
        
        // Let's just let UB3R-BOT handle that.
        if (tokens.length >= 3) {
          bot.sendMessage(channel, ".roll" + tokens[2]);
        } else {
          sendErrorMessage(bot, channel);
        }
      break;
      
      // TODO: Get an osu! API key
      // TODO: Fetch osu! profile info
      // TODO: Beatmap analysis system
      // TODO: pp calculator integration
      // TODO: pp what-ifs
      
      default:
        sendErrorMessage(bot, channel);
    }
    
  }
  
  private static final void sendCommandList(final RathBot bot, final IChannel channel) {
    bot.sendMessage(channel, COMMAND_LIST);
  }
  
  private static final void sendErrorMessage(final RathBot bot, final IChannel channel) {
    bot.sendMessage(channel, "Unknown command.");
  }
}
