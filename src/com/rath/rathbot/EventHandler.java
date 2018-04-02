
package com.rath.rathbot;

import com.rath.rathbot.log.MessageLogger;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class handles bot events, such as reading messages and responsing to them.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class EventHandler {
  
  /** The prefix for all commands. */
  private static final String COMMAND_PREFIX = "rb!";
  
  /**
   * Message received handler.
   * 
   * @param event contains event details.
   */
  @EventSubscriber
  public void onMessageReceived(MessageReceivedEvent event) {
    
    // Let's not respond to bots
    final IUser author = event.getAuthor();
    if (author.isBot()) {
      return;
    }
    
    // Log the message
    final IMessage message = event.getMessage();
    MessageLogger.logMessage(message);
    
    // Parse commands if it starts with the command prefix
    final String messageString = message.getContent();
    if (messageString.startsWith(COMMAND_PREFIX)) {
      
      // TODO: Spin up a new thread here so we can accept new commands while we process, unless that's already handled
      // by this method.
      CommandParser.parseCommand(message);
    }
    
  }
  
  /**
   * Prints message details.
   * 
   * @param evt contains event details.
   */
  @SuppressWarnings("unused")
  private final void logMessageDetailsToConsole(final MessageReceivedEvent evt) {
    final String author = evt.getAuthor().getName();
    final String channel = evt.getChannel().getName();
    final String message = evt.getMessage().getContent();
    System.out.println("Received message from " + author + " in #" + channel + ":");
    System.out.println("\"" + message + "\"");
  }
}
