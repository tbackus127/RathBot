
package com.rath.rathbot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * This class handles bot events, such as reading messages and responsing to them.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class EventHandler {
  
  private static final String COMMAND_PREFIX = "rb!";
  
  private final RathBot bot;
  
  public EventHandler(final RathBot bot) {
    this.bot = bot;
  }
  
  /**
   * Message received handler.
   * 
   * @param event contains event details.
   */
  @EventSubscriber
  public void onMessageReceived(MessageReceivedEvent event) {
    logMessageDetails(event);
    final String message = event.getMessage().getContent();
    if (message.startsWith(COMMAND_PREFIX)) {
      CommandParser.parseCommand(this.bot, event.getChannel(), event.getAuthor(), message);
    }
  }
  
  /**
   * Prints message details.
   * 
   * @param evt contains event details.
   */
  private void logMessageDetails(final MessageReceivedEvent evt) {
    final String author = evt.getAuthor().getName();
    final String channel = evt.getChannel().getName();
    final String message = evt.getMessage().getContent();
    System.out.println("Received message from " + author + " in #" + channel + ":");
    System.out.println("\"" + message + "\"");
  }
}
