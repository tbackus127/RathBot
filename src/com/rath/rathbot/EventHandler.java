
package com.rath.rathbot;

import com.rath.rathbot.data.MessageLogger;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This class handles bot events, such as reading messages and responsing to them.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class EventHandler {
  
  /** The prefix for all commands. */
  private static final String COMMAND_PREFIX = "rb!";
  
  /** A reference to the bot. */
  private final RathBot bot;
  
  /**
   * Default constructor.
   * 
   * @param bot a reference to an already-built RathBot.
   */
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
    
    final IMessage message = event.getMessage();
    final String messageString = message.getContent();
    if (messageString.startsWith(COMMAND_PREFIX)) {
      CommandParser.parseCommand(this.bot, event.getChannel(), event.getAuthor(), messageString);
    }
    
    // TODO: do spam filtering stuff here
    
    // TODO: do message logging here
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
